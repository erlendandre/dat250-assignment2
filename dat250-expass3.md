# DAT250 – SPA

[Frontend repository](https://github.com/erlendandre/dat250-assignment2/tree/main/frontend)  

[Full repository](https://github.com/erlendandre/dat250-assignment2)


This project integrates a **Spring Boot backend** with a **Svelte frontend** allowing:
- User registration and login (or guest without login/user)
- Creating polls (public or private)
- Adding vote options
- Inviting users to private polls
- Submitting votes
- Displaying vote counts

## Technical challenges and solutions

### Frontend integration
- Initially, vote counts were not visually updating immediately 
  **Solution:** Updated `Vote.svelte` to push the new vote into `selectedPoll.votes` locally after a successful POST request
- Displaying vote percentages required computing totals locally from the `selectedPoll.votes` array

### Backend and API
- Spring Boot REST endpoints were extended to return polls with votes and options.
- Validation was implemented via Jakarta Bean Validation to ensure:
  - Polls have at least 2 options
  - Private polls only allow invited users
  - Username etc is mando
- CORS with `@CrossOrigin` annotations allows frontend fetch requests during development

### Build and deployment
- Integrated frontend build into Gradle:
  - `npm run build` produces `dist/` folder
  - `build.gradle.kts` copies frontend assets to `src/main/resources/static`
- Issues with Gradle task ordering (`processResources`) were solved by defining proper task dependencies
- Frontend development with still possible via `npm run dev` in the frontend directory

### Pending issues
- Full authentication not implemented, currently uses `currentUser` object as demo
- UI enhancements (invite list search/filter, responsive layout)
- Further testing required to enforce validation and prevent double-voting etc
- Minor Node/Svelte errors like `node:util` import warning is ignored per now

## Running the application
From the backend root:
```bash
./gradlew bootRun
```

Gradle automatically builds the frontend if necessary and copies static assets.
The application runs on http://localhost:8080.

## Future Improvements
1. Authentication for secure sessions
2. Implement database
2. Enhance UI for private poll invitations and responsive design
3. Further testing and enforcement of all validation rules