# Design Document: TSX Plugin System for Kvaesitso

**QuickJS / Hermes + React**

**Version:** 0.1  
**Target:** [CephandriusMaxtori/Kvaesitso](https://github.com/CephandriusMaxtori/Kvaesitso)  
**Date:** 2026-10-07  
**Status:** Draft

---

## 1. Goals

Make it dramatically easier to extend Kvaesitso without writing full Android apps.

**Primary goals:**

- Allow plugins to be written in **TypeScript + JSX (TSX)**
- Support both **data providers** (search, weather, files, etc.) and **custom UI** (result cards, small widgets)
- Keep the runtime small, fast, and secure
- Maintain compatibility with the existing ContentProvider-based plugin system
- Enable hot-reload during development

**Non-goals (for v1):**

- Full React Native or DOM
- Running arbitrary untrusted code with full device access
- Replacing the existing Kotlin plugin SDK

---

## 2. High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Kvaesitso (Kotlin)                   │
│                                                          │
│  ┌─────────────────┐     ┌───────────────────────────┐  │
│  │ Plugin Manager  │────▶│  Existing Search System    │  │
│  │ (discovery,     │     │  + Result Rendering        │  │
│  │  loading,       │     └───────────────────────────┘  │
│  │  lifecycle)     │                                     │
│  └────────┬────────┘                                     │
│           │                                              │
│           ▼                                              │
│  ┌───────────────────────────────────────────────────┐  │
│  │              JS Runtime Host                       │  │
│  │  • Hermes (preferred) or QuickJS                   │  │
│  │  • React Custom Renderer                           │  │
│  │  • Typed Bridge (Kotlin ↔ JS)                      │  │
│  │  • Permission & Sandbox Enforcement                │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

Each plugin runs in its **own isolated JS context**.

---

## 3. Plugin Format

A plugin is a directory (or `.kvplugin` zip):

```
my-cool-plugin/
├── plugin.json          # Required metadata
├── index.js             # Compiled entry point (or index.tsx in dev)
├── assets/              # Optional icons, images, etc.
│   └── icon.png
└── (optional) README.md
```

**`plugin.json` example:**

```json
{
  "id": "com.example.coolsearch",
  "name": "Cool Search",
  "version": "1.0.0",
  "description": "Searches cool things",
  "author": "You",
  "main": "index.js",
  "provides": ["search"],
  "permissions": ["network"],
  "minLauncherVersion": "1.41.0",
  "icon": "assets/icon.png"
}
```

Supported `provides` values (v1):

- `search`
- `weather` (later)
- `files` (later)
- `custom-result-ui`

---

## 4. Runtime Choice

| Engine     | Pros                                      | Cons                          | Recommendation |
|------------|-------------------------------------------|-------------------------------|----------------|
| **Hermes** | Excellent Android performance, bytecode, used by RN | Slightly larger               | **Preferred** |
| QuickJS    | Extremely small, pure C, easy to embed    | No bytecode, slower startup   | Fallback / research |

**React approach:** Custom renderer (not React Native, not full DOM).  
Only a minimal set of primitives will be exposed:

- `View`
- `Text`
- `Image`
- `Pressable` / `Touchable`
- Basic layout props (`flex`, `padding`, `gap`, etc.)

This keeps the runtime small and gives us full control over the visual language.

---

## 5. Plugin Developer API (TypeScript)

```ts
// @kvaesitso/plugin

export interface SearchResult {
  id: string;
  title: string;
  subtitle?: string;
  icon?: string | number;          // url or asset id
  score?: number;
  onAction?: () => void;
  // optional custom component
  render?: (props: { result: SearchResult }) => JSX.Element;
}

export function createSearchProvider(config: {
  id: string;
  name: string;
  icon?: string;
  search: (query: string, signal?: AbortSignal) => Promise<SearchResult[]>;
}): SearchProvider;

// Later:
export function createWeatherProvider(...)
export function createFileProvider(...)
```

**Example plugin:**

```tsx
import { createSearchProvider } from "@kvaesitso/plugin";

export default createSearchProvider({
  id: "example-search",
  name: "Example",
  async search(query) {
    const res = await fetch(`https://api.example.com?q=${encodeURIComponent(query)}`);
    const data = await res.json();
    return data.map((item: any) => ({
      id: item.id,
      title: item.name,
      subtitle: item.description,
      icon: item.image,
      onAction: () => openUrl(item.url),
    }));
  },
});
```

---

## 6. Kotlin ↔ JS Bridge

The bridge will be strongly typed and minimal.

**Kotlin → JS**

- Inject host objects (`console`, `fetch` (permission-gated), `openUrl`, `showToast`, etc.)
- Call plugin functions (`search(query)`)

**JS → Kotlin**

- Return search results as structured data
- Request UI rendering of custom components
- Request permissions / capabilities

Communication will use a simple message-passing protocol (JSON or a more efficient binary format later).

---

## 7. Security Model

- Each plugin runs in an isolated JS context
- Permissions are declared in `plugin.json` and enforced by the host
- `fetch` / network is gated
- No direct access to Android APIs unless explicitly exposed through the bridge
- Plugins cannot access other plugins’ data
- Ability to disable / uninstall plugins easily
- Optional: code signing or hash verification for distributed plugins

---

## 8. Integration with Existing Kvaesitso

1. **Plugin discovery**  
   Scan a directory (`/plugins` or app-specific storage) + support installing `.kvplugin` files.

2. **Search pipeline**  
   TSX search providers are registered alongside the existing ContentProvider plugins. Results are merged and ranked normally.

3. **Result rendering**  
   - Default results use the existing Compose UI
   - If a result provides a custom `render` function, the React custom renderer draws it into a Compose surface

4. **Lifecycle**  
   Plugins are loaded lazily and can be unloaded when not needed.

---

## 9. Development Workflow

**For plugin authors:**

```bash
npx create-kvaesitso-plugin my-plugin
cd my-plugin
npm run dev          # watches + hot reloads into a running launcher
npm run build        # outputs production-ready plugin
```

**For launcher developers:**

- A debug toggle to enable the TSX runtime
- Logging bridge so `console.log` from plugins appears in logcat
- Ability to force-reload plugins

---

## 10. Implementation Phases

### Phase 1 – MVP (recommended first target)

- Hermes (or QuickJS) embedding
- Basic bridge
- Load a single JS file that exports a `search` function
- Return plain results (no custom UI yet)
- Manual plugin loading from a folder

### Phase 2

- Full `plugin.json` support
- Permission system
- `createSearchProvider` helper
- Hot reload

### Phase 3

- Custom result UI via React custom renderer
- Weather / file provider support
- Plugin installation UI inside the launcher

### Phase 4

- Plugin store / repository support
- TypeScript declaration packages
- Better tooling

---

## 11. Open Questions

1. Hermes vs QuickJS as the primary engine?
2. How aggressive should we be with the custom React renderer (how many components in v1)?
3. Should plugins be allowed to use a small set of pre-approved npm packages, or stay completely self-contained?
4. Storage location for installed plugins?
5. Do we want bytecode (Hermes) distribution from day one?

---

## 12. Next Actions

- Decide on primary JS engine (Hermes recommended)
- Create the initial module structure (`plugins/tsx-runtime`, `plugins/tsx-bridge`, etc.)
- Spike: embed Hermes and call a simple JS function from Kotlin
- Write the first TypeScript type definitions
