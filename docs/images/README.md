# ROSTRY Image Assets

This directory contains visual assets for the ROSTRY project documentation.

## Directory Structure

- `architecture/`: Technical diagrams (Mermaid exports, etc.)
- `screenshots/`: High-resolution app previews
- `workflows/`: UI flow and process diagrams
- `ui/`: Component-level visual documentation

## Asset Naming Conventions

To maintain consistency, please follow these naming patterns:

- **Screenshots**: `<feature>_<sub-feature>_preview.png` (e.g., `digital_farm_isometric_preview.png`)
- **Diagrams**: `<system_component>_<process>_diagram.png` (e.g., `auth_otp_flow_diagram.png`)
- **UI Components**: `ui_<component_name>_spec.png`

## Guidelines

1. **Format**: Prefer `.png` for screenshots and `.svg` for diagrams where possible.
2. **Size**: Keep file sizes optimized for web (under 500KB per image).
3. **Alt Text**: Always provide descriptive alt text when embedding in markdown.
4. **Placeholders**: If an image is pending, use the `> [!NOTE]` callout in the markdown file instead of a blank image link.

---

*For help with assets, contact the design lead or documentation maintainer.*
