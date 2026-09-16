# AI usage

During the development of this project, I challenged myself to rely on AI less than usual. Still, it was a helpful tool
for reviewing my ideas, getting unstuck on tricky bugs. I used Gemini for small,
isolated tasks and Codex (GPT-5.6 Sol/Terra) agents for broader questions.

Note: This overview highlights the "more important" prompts. I also used AI for some small questions which I haven't
listed here to keep this document concise.

## Gemini uses and prompts

1. Had some trouble with a DB query and needed to get unstuck.
    - Prompt: How to collect multiple rows into one array using Ktor Exposed and SQLite? I have table 1 that has id,
      name and another attribute and another table that has the id from table 1 that can appear for multiple attributes
      (rows). I want those attributes to be combined into one array.
    - Result: Got the general idea for moving forward.

2. After a whole day of coding, needed help with the message for one of the PRs.
    - Prompt: Minimal changes for this PR?
    ```
    Started saving form responses to DB. Added session handling to retrieve the saved responses from DB.
    Part of the PR was also a rename from session to form response and moving from endpoint querying to using Thymeleaf for server-side rendering of sectors.
    Related: #7
    ```
    - Result: The first response was "too AI" as I wanted to keep the descriptions very simple, but after further
      instructions, I got the expected suggestions.

## Codex CLI uses and prompts

Since the Codex agent has access to the repository, I didn't need to provide as much context for it to give useful
answers.

1. At the start of the repo, I wanted to make sure that I hadn't missed anything crucial.
    - Prompt: Please review the contents of the current repo. This is an assignment I am doing for a job application. I
      am trying to make the project management as good as possible. Does the start look okay? I plan to make issues,
      pull requests, atomic commits etc. Thought that these first 3 commits would be okay straight in main.

    ```
    Thought I would make myself an issue so I do not forget about this later: 

    ### Description
    After the form has been fixed and adjusted, review the page's accessibility to make sure it takes everyone into consideration.

    ### Acceptance criteria

    - [ ] Form can be used with only a keyboard
    - [ ] Page is accessible to screen readers
    - [ ] Page passes colorblindness tests
    ```

    - Result: Helped me write the main issues.

2. Let it review my repository for what should be included in `.gitignore`.
3. Let it clean up code leftover from the project template.
4. Used it to set up SQLite in the project.
    - Prompt: I cannot figure out what I should do to set up an SQLite DB for this project. Do not do the work for me
      but help me do it.
    - Result: Realised that it had been as easy as it seemed all along.
5. Had it write and review tests.
    - Prompt:

    ```
    Review this repository and add a sensible set of automated tests for the existing functionality.

    First, inspect the project structure, existing test setup, build configuration, and implementation before making changes. Follow the testing conventions and tools already used by the project. If no tests
    exist yet, use the standard testing approach for the project's framework/build system.

    Focus on tests that provide real value:

    * Core business logic and important behavior
    * Validation and edge cases
    * Failure/error cases where relevant
    * Repository/database behavior if it contains meaningful custom logic
    * HTTP/API behavior if the project exposes endpoints
    * Any areas that are easy to regress

    Avoid:

    * Testing trivial getters/setters or framework behavior
    * Tests that only exist to increase coverage numbers
    * Excessive mocking when a simpler test would be clearer
    * Large refactors of production code just to make it testable
    * Adding unnecessary testing libraries or infrastructure

    Keep the scope appropriate for a small project. Prefer a small number of clear, maintainable tests over trying to cover everything.

    While working:

    1. Identify the most important behaviors worth testing.
    2. Add the relevant tests using the project's existing style and conventions.
    3. Make only minimal production-code changes if they are genuinely necessary for testing or to fix a bug exposed by a test.
    4. Run the complete test suite.
    5. Fix any test failures caused by your changes.

    When finished, give me a short summary containing:

    * What you tested
    * Which files you added or changed
    * Any important behavior that is still untested and why
    * The command used to run the tests
    * Whether all tests pass
    ```

    - Result: Most of the code is covered one way or another, although more thorough tests could be implemented.

6. Had it review my changes and progress toward completing the task.
7. Had it style the form.
    - Prompt: Make minor adjustments to make the index.html look a bit better.
    - And then: Make it barely not show the user agent styles. Not a full CS 101 "my first form" like it is now.
    - Result: Got the barely styled look I was going for, although I still had to make some adjustments myself.