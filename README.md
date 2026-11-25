# Gugugaga Remastered — Fabric 1.21.11 source project

This is a Gradle/Fabric source project intended to build a JAR through GitHub Actions or locally.

## Build

- Minecraft Java: 1.21.11
- Java: 21
- Run `gradle build` (or use the included GitHub Actions workflow).
- The resulting JAR is in `build/libs/`.

## Included in this source project

- Gugu Gaga entity registration and spawn egg
- Custom Java entity renderer and model loader
- Converted model/texture resources and source animation clips
- Original add-on sound files and sound event definitions
- Fish feeding, taming chance, healing, sitting and follow-owner behaviour
- GitHub Actions workflow at `.github/workflows/build.yml`

## Important status notes

This ZIP is source code, not a precompiled or verified JAR. It has not been compiled in this environment. Run the GitHub Actions workflow and check the build log before treating it as build-ready.

The current renderer/model conversion is a custom Java renderer; it does not use EMF/ETF as a required dependency. EMF/ETF do not automatically fix a custom Fabric entity renderer. The animation loader currently blends idle and walk; other source animation clips are included but are not yet connected to gameplay states. The per-face Bedrock UV mapping and final in-game texture appearance still need verification/fixing in Minecraft.

The current entity supports fish feeding, taming, owner following and sitting. It does not yet implement every requested behaviour or animation.

## GitHub upload

Extract this ZIP and upload the *contents* of the folder to the repository. Keep `.github/workflows/build.yml` and `.gitignore` with their leading dots. If the GitHub website file picker hides dotfiles, use GitHub Desktop or Git from the extracted folder.

## Credits and permissions

The original Bedrock resource-pack manifest identifies the add-on as by DLT_Craft. Please obtain/check permission before redistributing the original model, texture, animations or sounds. Attribution does not itself grant redistribution rights.
