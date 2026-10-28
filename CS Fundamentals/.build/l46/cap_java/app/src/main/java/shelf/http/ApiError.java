package shelf.http;

import java.util.List;

public final class ApiError extends RuntimeException {
    final int status;
    final String title;
    final List<String> errors;                               // null unless it's a validation error

    public ApiError(int status, String title, String detail) {
        this(status, title, detail, null);
    }

    ApiError(int status, String title, String detail, List<String> errors) {
        super(detail);
        this.status = status;
        this.title = title;
        this.errors = errors;
    }

    static ApiError notFound(String path) {
        return new ApiError(404, "Not Found", "No resource at " + path);
    }

    static ApiError conflict(String detail) {
        return new ApiError(409, "Conflict", detail);
    }

    static ApiError invalid(List<String> errors) {
        return new ApiError(400, "Bad Request", errors.size() + " problem(s) with the request", errors);
    }
}
