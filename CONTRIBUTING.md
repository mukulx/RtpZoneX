# Contributing to RtpZoneX

Thank you for your interest in contributing to RtpZoneX! This document provides guidelines for contributing to the project.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/RtpZoneX.git`
3. Create a new branch: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Test your changes thoroughly
6. Commit your changes: `git commit -m "Add your feature"`
7. Push to your fork: `git push origin feature/your-feature-name`
8. Create a Pull Request

## Development Setup

### Prerequisites
- Java 21 or higher
- Gradle 8.5 or higher (wrapper included)
- Paper/Folia server for testing

### Building
```bash
./gradlew build
```

### Testing
Test your plugin on both:
- Paper server (Bukkit compatibility)
- Folia server (Folia compatibility)

## Code Style

- Follow Java naming conventions
- Use 4 spaces for indentation
- Add comments for complex logic
- Keep methods focused and concise
- Maintain the existing code structure

## Folia Compatibility

When contributing, ensure your code is compatible with both Bukkit and Folia:
- Use `SchedulerUtil` for all task scheduling
- Use region-based scheduling for entity/location operations
- Avoid global state modifications
- Test on both Paper and Folia servers

## Pull Request Guidelines

- Provide a clear description of the changes
- Reference any related issues
- Include screenshots/videos for UI changes
- Ensure all tests pass
- Update documentation if needed
- Keep PRs focused on a single feature/fix

## Reporting Issues

When reporting issues, please include:
- Server version (Paper/Folia)
- Plugin version
- Steps to reproduce
- Expected behavior
- Actual behavior
- Any error messages or logs

## License

By contributing, you agree that your contributions will be licensed under the GNU General Public License v3.0.
