"""Android Platform Adapter for Hermes Evolution Android.

Bridges the original Nous Research Hermes Agent engine to the Android platform,
specifically targeting Samsung Galaxy M36 5G (Android 16, ARM64, non-rooted).

Responsibilities:
1. Initialize and enforce scoped storage boundaries ($HERMES_HOME in context.filesDir).
2. Override POSIX desktop commands (sudo, xdg-open, open) with Android-compliant adapters.
3. Configure shell execution for Android Bionic (/system/bin/sh).
4. Redact credentials and enforce security boundaries.
"""

from __future__ import annotations

import os
import sys
import logging
from pathlib import Path
from typing import Dict, Any, Optional

logger = logging.getLogger("hermes.android.platform")

class AndroidPlatformAdapter:
    """Manages platform-specific environment settings and patches for Android runtime."""

    def __init__(self, app_files_dir: str, cache_dir: Optional[str] = None):
        self.app_files_dir = Path(app_files_dir).resolve()
        self.cache_dir = Path(cache_dir or (self.app_files_dir / "cache")).resolve()
        self.hermes_home = self.app_files_dir / "hermes"
        self.workspaces_dir = self.app_files_dir / "workspaces"

    def bootstrap(self) -> Dict[str, str]:
        """Apply Android runtime patches and initialize directory structures."""
        # Ensure directories exist
        self.hermes_home.mkdir(parents=True, exist_ok=True)
        self.workspaces_dir.mkdir(parents=True, exist_ok=True)
        self.cache_dir.mkdir(parents=True, exist_ok=True)

        # Set environment variables
        os.environ["HERMES_HOME"] = str(self.hermes_home)
        os.environ["TMPDIR"] = str(self.cache_dir)
        os.environ["HOME"] = str(self.app_files_dir)
        os.environ["TERMINAL_ENV"] = "android"

        # Android 16 non-rooted security flags
        os.environ["HERMES_NO_SUDO"] = "1"
        os.environ["HERMES_PLATFORM"] = "android"

        # Apply path to hermes_constants if importable
        try:
            import hermes_constants
            hermes_constants.set_hermes_home_override(self.hermes_home)
            logger.info("Hermes home override bound to %s", self.hermes_home)
        except ImportError:
            logger.debug("hermes_constants not in immediate path during early adapter init")

        # Patch shell path if /bin/sh is missing (standard Android Bionic location is /system/bin/sh)
        if not os.path.exists("/bin/sh") and os.path.exists("/system/bin/sh"):
            os.environ["SHELL"] = "/system/bin/sh"

        return {
            "hermes_home": str(self.hermes_home),
            "workspaces_dir": str(self.workspaces_dir),
            "cache_dir": str(self.cache_dir),
            "shell": os.environ.get("SHELL", "/system/bin/sh"),
            "platform": "Android 16 (ARM64)",
            "is_rooted": "false"
        }

    def validate_path_safety(self, candidate_path: str | Path, workspace_root: Optional[Path] = None) -> Path:
        """Prevent path traversal outside authorized scoped workspace and hermes directories."""
        resolved = Path(candidate_path).resolve()
        allowed_roots = [self.workspaces_dir, self.hermes_home, self.cache_dir]
        if workspace_root:
            allowed_roots.insert(0, Path(workspace_root).resolve())

        is_safe = any(resolved == root or root in resolved.parents for root in allowed_roots)
        if not is_safe:
            raise PermissionError(
                f"Path access denied: '{resolved}' is outside permitted Android application sandbox "
                f"({[str(r) for r in allowed_roots]})."
            )
        return resolved

    def redact_secrets(self, text: str) -> str:
        """Scrub known API keys and sensitive patterns from logs or terminal outputs."""
        import re
        patterns = [
            (r"(sk-[a-zA-Z0-9_-]{20,})", "sk-***REDACTED***"),
            (r"(AIzaSy[a-zA-Z0-9_-]{20,35})", "AIzaSy***REDACTED***"),
            (r"(gsk_[a-zA-Z0-9_-]{20,})", "gsk_***REDACTED***"),
            (r"(ghp_[a-zA-Z0-9]{36})", "ghp_***REDACTED***"),
            (r"(Bearer\s+)[a-zA-Z0-9_.-]{20,}", r"\1***REDACTED***"),
        ]
        sanitized = text
        for pat, replacement in patterns:
            sanitized = re.sub(pat, replacement, sanitized)
        return sanitized
