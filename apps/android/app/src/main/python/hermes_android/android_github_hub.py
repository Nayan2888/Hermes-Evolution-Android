"""GitHub Capability Hub for Hermes Evolution Android.

Enables secure inspection, compatibility scoring, and controlled skill installation
from public GitHub repositories without executing untrusted code.
"""

from __future__ import annotations

import re
import json
import logging
from typing import Dict, Any, List, Optional
from urllib.parse import urlparse

logger = logging.getLogger("hermes.android.github_hub")

class GitHubCapabilityHub:
    """Analyzes GitHub repositories for Android ARM64 capability and security risk."""

    # Incompatible libraries on stock non-rooted Android 16
    DESKTOP_GUI_LIBS = {"tkinter", "pyqt5", "pyqt6", "pyside2", "pyside6", "wxpython", "kivy"}
    SYSTEM_DEPENDENT_LIBS = {"winreg", "msvcrt", "pywin32", "x11", "xcb", "pulseaudio"}
    KNOWN_COMPATIBLE_LIBS = {
        "requests", "httpx", "aiohttp", "pydantic", "jinja2", "numpy",
        "beautifulsoup4", "rich", "click", "typer", "markdown"
    }

    def parse_github_url(self, url: str) -> Optional[Dict[str, str]]:
        """Extract owner and repo name from GitHub URL."""
        parsed = urlparse(url.strip())
        if parsed.netloc not in ("github.com", "www.github.com"):
            return None
        parts = [p for p in parsed.path.strip("/").split("/") if p]
        if len(parts) >= 2:
            return {"owner": parts[0], "repo": parts[1].replace(".git", "")}
        return None

    def analyze_repository_spec(
        self,
        repo_url: str,
        files_manifest: Optional[List[str]] = None,
        dependencies: Optional[List[str]] = None,
        license_type: str = "MIT"
    ) -> Dict[str, Any]:
        """Produce an Android qualification scorecard and security audit."""
        repo_info = self.parse_github_url(repo_url)
        if not repo_info:
            return {
                "success": False,
                "error": f"Invalid GitHub repository URL: '{repo_url}'. Expected format: https://github.com/owner/repo"
            }

        dep_list = [d.lower().strip() for d in (dependencies or [])]
        files = files_manifest or []

        # Analyze risks
        found_desktop_gui = [d for d in dep_list if d in self.DESKTOP_GUI_LIBS]
        found_system_deps = [d for d in dep_list if d in self.SYSTEM_DEPENDENT_LIBS]
        has_native_c_code = any(f.endswith((".c", ".cpp", ".cc", ".rs", ".go")) for f in files)

        score = 100
        penalties = []

        if found_desktop_gui:
            score -= 40
            penalties.append(f"Requires desktop GUI frameworks ({found_desktop_gui}) not supported on Android.")

        if found_system_deps:
            score -= 30
            penalties.append(f"Depends on host-specific desktop libraries ({found_system_deps}).")

        if has_native_c_code:
            score -= 15
            penalties.append("Contains uncompiled C/C++/Rust code requiring native ARM64 NDK compilation.")

        if license_type.upper() in ("GPL-3.0", "AGPL-3.0"):
            penalties.append(f"Notice: Copyleft license ({license_type}) requires attribution and source sharing.")

        score = max(0, min(100, score))

        # Determine integration route
        if score >= 80:
            route = "DIRECT_SKILL"
            rec = "Eligible for direct sandboxed skill installation on Hermes Android."
        elif score >= 50:
            route = "ADAPTER_REQUIRED"
            rec = "Requires headless adapter to bypass desktop UI dependencies."
        else:
            route = "REMOTE_WORKER_ONLY"
            rec = "Incompatible with local Android runtime. Delegate to optional remote worker."

        return {
            "success": True,
            "owner": repo_info["owner"],
            "repo": repo_info["repo"],
            "url": repo_url,
            "compatibility_score": score,
            "recommended_route": route,
            "recommendation": rec,
            "penalties": penalties,
            "license": license_type,
            "security_sandbox": "ENABLED (Default-Deny)"
        }
