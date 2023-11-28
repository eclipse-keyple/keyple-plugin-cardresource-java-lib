# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0] - 2023-11-28
:warning: Major version! Following the migration of the "Calypsonet Terminal" APIs to the
[Eclipse Keypop project](https://keypop.org), this library now implements Keypop interfaces.
### Added
- Added project status badges on `README.md` file.
### Fixed
- CI: code coverage report when releasing.
### Upgraded
- Calypsonet Terminal Reader API `1.2.0` -> Keypop Reader API `2.0.0`
- Keyple Plugin API `2.1.0` -> `2.2.0`
- Keyple Service Resource Library `2.1.1` -> `3.0.0`
- Keyple Util Library `2.3.0` -> `2.3.1`

## [1.0.1] - 2023-04-27
### Fixed
- Exception handling associated with the reader allocation process.
### Upgraded
- "Keyple Service Resource Library" to version `2.1.1`

## [1.0.0] - 2023-04-25
This is the initial release.

[unreleased]: https://github.com/eclipse/keyple-plugin-cardresource-java-lib/compare/2.0.0...HEAD
[2.0.0]: https://github.com/eclipse/keyple-plugin-cardresource-java-lib/compare/1.0.1...2.0.0
[1.0.1]: https://github.com/eclipse/keyple-plugin-cardresource-java-lib/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/eclipse/keyple-plugin-cardresource-java-lib/releases/tag/1.0.0