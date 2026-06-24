# DSG Control Plane Android - Implementation Status & Mock Documentation

This document outlines the current state of the Android Orchestrator app, identifying which parts are connected to real backend services and which parts are currently mocked (simulated) for UI demonstration. This serves as a guide for future development.

## 1. Multi-Agent Execution Pipeline (Mocked)
**Location:** `AgiViewModel.kt` -> `startPipeline()`
*   **Current State:** The progress bar, task status updates, and the worker nodes (Hermes 3, Nemotron-4, Gemini 3.5 Flash) are currently simulated using `delay()` loops and hardcoded worker states. It does call the real DSG backend at the end (`executeDsgBackend`), but the intermediate steps are faked.
*   **Next Steps:** Wire this up to the real `/api/multi-agent/execute` or `/api/executions` endpoints on your Next.js backend. The Android app should poll or use WebSockets/SSE to get real-time status updates from the DSG orchestrator rather than faking the loading bars.

## 2. Sandbox Terminal (Mocked)
**Location:** `AgiViewModel.kt` -> `executeTerminalCommand()`
*   **Current State:** The terminal UI only responds to a few hardcoded commands (`echo`, `help`, `sandbox-test`).
*   **Next Steps:** Connect this to your real backend executor (e.g., `/api/agent/commands` or `/api/spine/execute` for Safe DOM/terminal commands) so that the terminal actually triggers real remote tools and returns the payload.

## 3. Supabase Telemetry Sync (Partially Real, Requires Schema Check)
**Location:** `AgiViewModel.kt` -> `syncLogToSupabase()`
*   **Current State:** Makes a real HTTP POST to Supabase using the configured `.env` credentials. However, it currently targets a table named `pipeline_logs`.
*   **Next Steps:** Update the endpoint in `syncLogToSupabase` to target your actual governance/audit tables defined in the DSG schema, such as `audit_logs`, `executions`, or `agi_action_audit`.

## 4. Architecture & Health Nodes UI (Static)
**Location:** `MainActivity.kt` -> Architecture blocks (DSG Gateway, Data Storage, etc.)
*   **Current State:** The UI shows static checks and statuses (e.g., "Policy ID: 894x", "Z3 Runtime Proofs").
*   **Next Steps:** Fetch real health checks, policy IDs, and metrics from your backend (`/api/health`, `/api/agent/status`) to populate these dashboard tiles dynamically.
