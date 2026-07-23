## Summary of changes

-   Fixed SQL filtering logic in both the Spring Data repository and the
    Oracle PL/SQL reference by grouping the title/description search
    conditions so the status filter is applied correctly.
-   Removed the artificial API response delay from the controller to
    improve perceived performance.
-   Reset pagination to the first page whenever the search term or
    status filter changes.
-   Improved the React data-fetching hook by clearing previous errors
    before each request and using `finally()` to consistently reset the
    loading state.

## What I chose not to change

-   I did not refactor pagination to use Spring Data `Pageable` because
    it would require broader repository and API changes, while the
    assignment emphasized a focused patch.
-   I did not add request debouncing or request cancellation
    (`AbortController`) because they are architectural improvements
    rather than bug fixes.
-   I left the Oracle pagination approach (`ROWNUM`) unchanged because
    the file explicitly documents it as a pre-12c compatibility pattern.

## Biggest remaining risk

The application performs pagination in memory after retrieving all
matching rows from the database. This approach will not scale well as
the dataset grows and should eventually be replaced with database-level
pagination.

## AI usage

I used ChatGPT to review the codebase, discuss potential bugs, compare
solution options, and validate my reasoning. I implemented the selected
fixes, tested them locally, and verified that I understood every change
before committing it.
