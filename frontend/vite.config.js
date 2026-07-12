import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import { fileURLToPath } from "url";

// This safely resolves the path to KatibaAi from inside the frontend folder
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export default defineConfig({
  plugins: [react()],
  // Looks up one level from 'frontend' into 'KatibaAi'
  envDir: path.resolve(__dirname, "../"),
});
