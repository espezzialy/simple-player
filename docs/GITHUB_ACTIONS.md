# GitHub Actions: CI and APK builds

This project includes two workflows under `.github/workflows/`.

## 1. CI (`ci.yml`)

**When it runs:** on every **push** to `main` and on every **pull request** targeting `main`.

**What it does:**

- `./gradlew :app:testDebugUnitTest` — unit tests (fails if any test fails).
- `./gradlew :app:ktlintCheck` — Kotlin style checks (fails on violations).

**How to see results:** in the GitHub repo → **Actions** tab → **CI** workflow → open the latest run.

**Requirement:** the code must be on GitHub (remote) and the `main` branch must exist. Pushing to `main` triggers the workflow automatically.

---

## 2. Build APK (`build-apk.yml`)

**When it runs:**

- **Manually:** **Actions** → **Build APK** → **Run workflow** (pick the branch, usually `main`).
- **On tag:** when you push a tag starting with `v` (e.g. `git tag v1.0.0 && git push origin v1.0.0`).

**What it does:** builds a **debug** APK (`assembleDebug`) and uploads it as an artifact named `simpleplayer-debug-apk`.

**How to download the APK:**

1. **Actions** → **Build APK** → open a successful run.
2. Under **Artifacts**, download `simpleplayer-debug-apk` (ZIP containing the `.apk`).

---

## 3. Enabling workflows on GitHub

1. Commit and push the files under `.github/workflows/`.
2. In the repo: **Settings** → **Actions** → **General** → under **Workflow permissions**, choose **Read and write permissions** (only needed if you later add workflows that create releases or issues automatically; the default is usually enough for CI and artifacts).
3. Make sure **Actions** are allowed for the repository (and not disabled at the org level).

---

## 4. GitHub Release with the APK

Simple manual flow:

1. Run the **Build APK** workflow (or push a `v*` tag to trigger it automatically).
2. Download the `simpleplayer-debug-apk` artifact.
3. On GitHub: **Releases** → **Create a new release** → choose or create a tag → attach the `.apk` under **Attach binaries** → publish.

The generated APK is **debug** (signed with the CI debug keystore). It is fine for testing and internal distribution; **do not** ship this APK as-is to the Play Store.

---

## 5. Signed release APK (Play Store / “official” distribution)

`assembleRelease` usually requires **signing** (keystore). The current workflow does **not** configure that. For a signed release in CI:

1. Generate a keystore locally and store the passwords and alias as **GitHub Actions secrets** (**Settings** → **Secrets and variables** → **Actions**), e.g. `RELEASE_KEYSTORE_BASE64`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.
2. In `app/build.gradle.kts`, add a `signingConfigs.release` block that reads those values when present (common Android pattern).
3. In the build workflow, add steps that decode the keystore and run `./gradlew assembleRelease`, then upload the APK from `app/build/outputs/apk/release/`.

If you want this wired in Gradle and YAML, ask with the exact secret names you prefer.

---

## 6. Notes

- Workflows use **JDK 17** and **Ubuntu** (`ubuntu-latest`), aligned with AGP 8.x.
- The APK job installs `platforms;android-36` and `build-tools;35.0.0` to match this project’s `compileSdk = 36`. If you change `compileSdk`, update the **Android SDK** → `packages` section in the workflow.
- **Branch protection (optional):** under **Settings** → **Branches**, you can require the **CI** workflow to pass before merging into `main`.
