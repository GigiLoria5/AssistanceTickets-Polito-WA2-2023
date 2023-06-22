export const getErrorMessage = (statusCode) => {
    switch (statusCode) {
        case 401:
            return "Unauthorized";
        case 404:
            return "Resource not found";
        default:
            return `Server returned status code ${statusCode}`;
    }
};

export const handleErrorResponse = async (response) => {
    const statusCode = response.status;
    let errorMessage;

    if (statusCode === 401 || statusCode === 404) {
        errorMessage = getErrorMessage(statusCode);
    } else {
        const errorResponse = await response.json();
        errorMessage = errorResponse.error;
    }

    return {error: errorMessage, status: statusCode};
};
