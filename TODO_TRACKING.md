# TODO Tracking

This document tracks remaining TODO items that need to be addressed in future development cycles.

## Format
Each TODO entry follows this format:
- **ID**: Unique identifier
- **Description**: Brief description of the task
- **Priority**: High, Medium, or Low
- **Status**: Open, In Progress, or Completed
- **Notes**: Additional context

---

## Open TODOs

### Authentication

**AUTH-001: Remove remaining phone auth references**
- Description: Remove PHONE_INPUT and OTP_VERIFICATION steps from LoginStep enum and LoginViewModel
- Priority: Medium
- Status: Open
- Notes: Phone auth is deprecated, only Google Sign-In is supported

### Community Engagement

**COMM-001: Add getFriendsCommunities to CommunityRepository**
- Description: Implement getFriendsCommunities method in CommunityRepository interface and implementation
- Priority: Medium
- Status: Open
- Notes: Required by CommunityEngagementService.getCommunitySuggestions

**COMM-002: Add getUserCommunities to CommunityRepository**
- Description: Implement getUserCommunities method in CommunityRepository interface and implementation
- Priority: Medium
- Status: Open
- Notes: Required by CommunityEngagementService.getCommunitySuggestions

### UI/UX

**UI-001: Extract remaining hardcoded strings**
- Description: Find and extract all remaining hardcoded strings in UI components to strings.xml
- Priority: Low
- Status: Open
- Notes: Check AuthWelcomeScreen.kt and other UI files

### Performance

**PERF-001: Optimize CommunityRepository.getUserGroups**
- Description: Fix the combine query in getUserGroups that passes empty string to streamMembers
- Priority: Medium
- Status: Open
- Notes: Current implementation has a bug - should use getUserGroupIds instead

---

## Completed TODOs

### Phone Auth Cleanup (Completed: 2024-01-XX)

- Removed StartPhoneVerificationUseCase.kt
- Removed VerifyOtpUseCase.kt
- Removed ResendOtpUseCase.kt
- Removed LinkPhoneUseCase.kt
- Removed PhoneAuthViewModel.kt
- Removed phoneauth directory

### String Resources (Completed: 2024-01-XX)

- Added login screen strings to strings.xml
- Updated LoginScreen.kt to use stringResource for all hardcoded text

---

## Notes

- TODO comments in code should follow format: `// TODO: [ID] Description`
- When creating GitHub issues, reference the TODO ID from this file
- Update this file when TODOs are completed or new ones are identified