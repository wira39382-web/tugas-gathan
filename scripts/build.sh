#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p out
if command -v javac >/dev/null 2>&1; then
  javac -encoding UTF-8 -Xlint:all -d out src/Menu.java src/Main.java
else
  java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 -Xlint:all -d out src/Menu.java src/Main.java
fi
