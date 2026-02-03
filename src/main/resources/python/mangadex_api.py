# MangaDex API Implementation in Python
# This module provides Python-based HTTP client for MangaDex API
# Used by Jython integration in MangaDexClient

import json
import sys

# Use Java's HttpURLConnection for HTTP requests (Jython 2.7 compatible)
from java.net import URL
from java.io import BufferedReader, InputStreamReader
from java.nio.charset import StandardCharsets

# API base URL
API_BASE = "https://api.mangadex.org"


def _make_request(url):
    """
    Make HTTP GET request and return JSON response.
    
    Args:
        url: The URL to request
        
    Returns:
        Parsed JSON data as Python dict/list
    """
    try:
        url_obj = URL(url)
        connection = url_obj.openConnection()
        
        connection.setRequestProperty('User-Agent', 'Shiori/1.0')
        connection.setRequestMethod('GET')
        connection.setConnectTimeout(30000)
        connection.setReadTimeout(30000)
        
        # Read response using Java's BufferedReader
        reader = BufferedReader(InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
        try:
            # Build string from reader
            sb = []
            line = reader.readLine()
            while line is not None:
                sb.append(line)
                line = reader.readLine()
            data = ''.join(sb)
            return json.loads(data)
        finally:
            reader.close()
            
    except Exception as e:
        sys.stderr.write("API request failed: {0}\n".format(e))
        raise


def search_manga(title):
    """
    Search for manga by title.
    
    Args:
        title: Search query string
        
    Returns:
        List of dicts with 'id' and 'title' keys
    """
    import urllib
    encoded_title = urllib.quote(title)
    url = "{0}/manga?limit=20&title={1}".format(API_BASE, encoded_title)
    
    try:
        root = _make_request(url)
        results = []
        
        for item in root.get("data", []):
            manga_id = item.get("id")
            titles = item.get("attributes", {}).get("title", {})
            
            # Prefer English title, fall back to first available
            name = titles.get("en")
            if not name and titles:
                name = titles.values()[0]
            
            if manga_id and name:
                results.append({
                    "id": manga_id,
                    "title": name
                })
        
        return results
    except Exception as e:
        sys.stderr.write("Search manga failed: {0}\n".format(e))
        return []


def get_manga(manga_id):
    """
    Get manga details by ID.
    
    Args:
        manga_id: The manga ID
        
    Returns:
        Dict with 'id' and 'title' keys, or None if not found
    """
    url = "{0}/manga/{1}".format(API_BASE, manga_id)
    
    try:
        root = _make_request(url)
        data = root.get("data", {})
        
        manga_id = data.get("id")
        titles = data.get("attributes", {}).get("title", {})
        
        # Prefer English title, fall back to first available
        name = titles.get("en")
        if not name and titles:
            name = titles.values()[0]
        
        if manga_id and name:
            return {
                "id": manga_id,
                "title": name
            }
    except Exception as e:
        sys.stderr.write("Get manga failed: {0}\n".format(e))
    
    return None


def get_chapters(manga_id):
    """
    Get list of chapters for a manga.
    
    Args:
        manga_id: The manga ID
        
    Returns:
        List of dicts with 'id', 'title', and 'number' keys
    """
    url = "{0}/chapter?manga={1}&translatedLanguage[]=en&order[chapter]=asc".format(API_BASE, manga_id)
    
    try:
        root = _make_request(url)
        chapters = []
        
        for item in root.get("data", []):
            attributes = item.get("attributes", {})
            chapters.append({
                "id": item.get("id"),
                "title": attributes.get("title", ""),
                "number": attributes.get("chapter", "")
            })
        
        return chapters
    except Exception as e:
        sys.stderr.write("Get chapters failed: {0}\n".format(e))
        return []


def get_page_urls(chapter_id):
    """
    Get page URLs for a chapter.
    
    Args:
        chapter_id: The chapter ID
        
    Returns:
        List of page image URLs
    """
    url = "{0}/at-home/server/{1}".format(API_BASE, chapter_id)
    
    try:
        root = _make_request(url)
        
        base_url = root.get("baseUrl")
        chapter_data = root.get("chapter", {})
        page_hash = chapter_data.get("hash")
        pages = chapter_data.get("data", [])
        
        if base_url and page_hash:
            return ["{0}/data/{1}/{2}".format(base_url, page_hash, page) for page in pages]
        
        return []
    except Exception as e:
        sys.stderr.write("Get page URLs failed: {0}\n".format(e))
        return []


def get_manga_stats(manga_id):
    """
    Get statistics for a manga.
    
    Args:
        manga_id: The manga ID
        
    Returns:
        Dict containing manga statistics or None
    """
    url = "{0}/statistics/manga/{1}".format(API_BASE, manga_id)
    
    try:
        root = _make_request(url)
        stats = root.get("statistics", {}).get(manga_id)
        return stats
    except Exception as e:
        sys.stderr.write("Get manga stats failed: {0}\n".format(e))
        return None

