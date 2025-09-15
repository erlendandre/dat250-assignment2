# DAT250 Experiment 6 – SPA Integration with Poll App

## Technical Problems Encountered

1. **Svelte Binding Issues**  
   Initially, there were errors like `Can only bind to state or props` when trying to bind checkboxes. This was resolved by using proper local variables with `bind:checked`.

2. **State Management and Current User**  
   When implementing private polls with invited users, it was necessary to pass the `currentUser` as a prop to the `CreatePoll` component to exclude the poll creator from the invite list. Initial attempts caused TypeScript errors like `Cannot find name 'currentUser'`. The solution was to declare `export let currentUser` in the component and pass it correctly from `App.svelte`.

3. **Fetching and Filtering Polls**  
   Public polls were not showing for logged-in users, and private polls were not filtered correctly. This required careful handling of the `invitedUserIds` property and conditional rendering based on the current user.

4. **Frontend Deployment Issues**  
   - Running `./gradlew buildFrontend` initially failed due to missing npm scripts and Node.js version mismatch.  
   - Node version 22.0.0 caused warnings; Vite requires >=22.12. Upgrading Node resolved warnings.  
   - Relative URLs had to replace `http://localhost:8080/...` for the SPA to work after deployment in Spring Boot.

5. **Gradle Task Conflicts**  
   There was a conflict with duplicate `npmInstall` tasks when trying to automate frontend build with Gradle. Removing redundant task definitions solved the issue.

## Experiment Code Links

- [Frontend repository](https://github.com/erlendandre/dat250-assignment2/tree/main/frontend)
- [Full repository](https://github.com/erlendandre/dat250-assignment2)

## Pending Issues

1. **User Authentication**  
   The current system does not fully implement secure login or session management. Users are identified only by a `currentUser` variable. Proper authentication and token management is pending.

2. **Vote Persistence Validation**  
   While poll creation works, vote counting and persistence for private polls may need further testing to ensure only invited users can vote and cannot vote twice.

3. **UI Enhancements**  
   The invite user list works, but UX improvements such as search/filter and responsive layout are not yet implemented.

4. **Build Automation**  
   Fully automating the frontend build and copy into Spring Boot via Gradle works but could be streamlined further. Current setup requires Node.js version 22.12+ for warning-free builds.