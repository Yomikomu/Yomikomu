# Recent Mangas Feature Implementation

## Plan
- [x] 1. Create RecentManga.kt - Data class for recent manga
- [x] 2. Create RecentMangasStore.kt - Persistence store
- [x] 3. Create RecentMangasPanel.java - UI panel for recent manga list
- [x] 4. Modify MainFrame.java - Integrate recent manga tracking
- [x] 5. Update TODO.md - Mark task as complete
- [x] 6. Test the feature (build SUCCESS)

## Implementation Details
### RecentManga.kt
- mangaId: String
- title: String
- lastReadAt: Long
- Max count: 10

### RecentMangasStore.kt
- Save/load from ~/.shiori/recent_mangas.json
- Methods: add(), getAll(), remove(), clear(), updateTitle()

### RecentMangasPanel.java
- JList showing recent manga
- Click to load chapters
- Scrollable list with refresh and clear buttons

## Features
- Automatically tracks manga when selected from search
- Persistent storage across app restarts
- Shows most recently read manga at the top
- Maximum 10 manga in the list
- Click to quickly access manga chapters

