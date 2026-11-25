# Gugugaga Remastered — Fabric 1.21.11

A Fabric mod project containing Gugu Gaga's converted geometry, texture, animation clips, Java entity/renderer, and a spawn egg.

## GitHub upload — read this first

If GitHub says `.github` or `.gitignore` is hidden / “No file chosen”, this is a limitation of the browser file picker, not a broken project. **Do not rename either item.** The workflow must stay at `.github/workflows/build.yml`, and `.gitignore` must keep its leading dot.

Recommended: extract this ZIP, install GitHub Desktop, then use **File → Add Local Repository** and select the extracted folder. Create a repository there if prompted, commit all files, and publish it. GitHub Desktop/Git includes hidden dotfiles automatically.

There is also an `UPLOAD_TO_GITHUB.bat` helper for Windows (requires Git for Windows). See `GITHUB_UPLOAD_GUIDE.txt` for exact steps. Do not upload the ZIP itself as a single file.

## Build target

- Minecraft Java: 1.21.11
- Java: 21
- Fabric Loader and Fabric API versions: `gradle.properties`
- Gradle build: `gradle build`

The GitHub Actions workflow in `.github/workflows/build.yml` builds the project and uploads `build/libs/*.jar` as an artifact. If the workflow fails, its build log is needed to diagnose any remaining Java/API errors.

## Current implementation status

- Registers a Gugu Gaga creature and a spawn egg in the Spawn Eggs creative tab.
- Includes the converted model geometry, texture, and all 15 animation clips from the source resource pack.
- The current Java animation controller only plays `idle` and `walk`. The other clips are preserved as source animation data but are **not yet wired to gameplay states**. This ZIP does not claim that all 15 animations are fully playable.
- The project has not been compiled or tested in a live Minecraft client in this environment. A successful GitHub Actions build and in-game test are still required before treating the JAR as verified.

## Credits and permissions

The original Bedrock resource-pack manifest identifies the add-on as **by DLT_Craft**. This port retains that attribution. Please check the original creator's licence/permission before publishing or redistributing the converted model, texture, or animations. Credit does not itself grant redistribution permission.
