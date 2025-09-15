# DAT250 – SPA Integration with Poll App

## Technical problems encountered

1. **Svelte binding issues**  
   Errors like `Can only bind to state or props` when trying to bind checkboxes. Resolved by using proper local variables with `bind:checked`.

2. **State management and current user**  
   When implementing private polls with invited users, it was necessary to pass the `currentUser` as a prop to the `CreatePoll` component to exclude the poll creator from the invite list. Initial attempts caused TypeScript errors like `Cannot find name 'currentUser'`. The solution was to declare `export let currentUser` in the component and pass it correctly from `App.svelte`.

3. **Fetching and filtering polls**  
   Public polls were not showing for logged-in users, and private polls were not filtered correctly. This required careful handling of the `invitedUserIds` property and conditional rendering based on the current user.

4. **Frontend feployment issues**  
   Running `./gradlew buildFrontend` initially failed due to missing npm scripts and Node.js version mismatch.  
   Node version 22.0.0 caused warnings; Vite requires >=22.12. Upgrading Node resolved warnings.  
   Relative URLs had to replace `http://localhost:8080/...` for the SPA to work after deployment in Spring Boot.

5. **Gradle task conflicts**  
   There was a conflict with duplicate `npmInstall` tasks when trying to automate frontend build with Gradle. Removing redundant task definitions solved the issue.

## Pending issues

1. **User authentication**  
   The current system does not fully implement secure login or session management. Users are identified only by a `currentUser` variable. Proper authentication and token management is pending.

2. **Vote persistence validation**  
   While poll creation works, vote counting and persistence for private polls may need further testing to ensure only invited users can vote and cannot vote twice.

3. **UI enhancements**  
   The invite user list works, but UX improvements such as search/filter and responsive layout are not yet implemented.

4. **Build automation**  
   Fully automating the frontend build and copy into Spring Boot via Gradle works but could be streamlined further. Current setup requires Node.js version 22.12+ for warning-free builds.

5. **Poll availability**  
   Can only access hardcoded test polls for now.. Working on this.


## Links

- [Frontend repository](https://github.com/erlendandre/dat250-assignment2/tree/main/frontend)
- [Full repository](https://github.com/erlendandre/dat250-assignment2)