# Images & Diagrams

This directory stores exported diagrams, screenshots, and other visual assets referenced by the documentation.

## Directory Structure
```
images/
├── architecture/     # Architecture diagrams
├── screenshots/      # App screenshots
├── workflows/        # Process/flow diagrams
├── ui-mockups/       # Design mockups
└── diagrams/         # General diagrams
```

## Guidelines
- Prefer **SVG** for diagrams, **PNG** for screenshots
- Use descriptive names, e.g. `marketplace-listing-view.png`, `rbac-architecture.svg`
- Keep files under ~1MB when possible; optimize images before committing
- Export Mermaid diagrams as images for environments without Mermaid support
- Include alt text in Markdown for accessibility

## Creating Diagrams
- Use Mermaid in docs and export via Mermaid CLI or editor extensions
- Tools: draw.io, Excalidraw, Figma

## Inventory

- architecture/
  - system-context.svg (pending/export later)
- screenshots/
  - home-screen.png (placeholder pending)
- workflows/
  - (none yet)

Note: If no assets are present yet, references in docs should be temporarily hidden or point to this README for status.

## Contributing
- Place new assets in the appropriate subfolder
- Update the referencing docs with relative paths
- Remove or replace outdated images when docs change
