import {HttpStatusCode} from "../../enums/HttpStatusCode";
import dayjs from "dayjs";

export const getErrorMessage = (statusCode) => {
    switch (statusCode) {
        case HttpStatusCode.UNAUTHORIZED:
            return "Unauthorized";
        case HttpStatusCode.NOT_FOUND:
            return "Resource not found";
        default:
            return `Server returned status code ${statusCode}`;
    }
};

export const handleErrorResponse = async (response) => {
    const statusCode = response.status;
    let errorMessage;

    if (statusCode === HttpStatusCode.UNAUTHORIZED || statusCode === HttpStatusCode.NOT_FOUND) {
        errorMessage = getErrorMessage(statusCode);
    } else {
        const errorResponse = await response.json();
        errorMessage = errorResponse.error;
    }

    return {error: errorMessage, status: statusCode};
};

export const handleApiError = (err, showError) => {
    if (err.status === HttpStatusCode.UNAUTHORIZED) {
        window.location.href = "/login";
    } else {
        showError(err.error);
    }
}

export const getAccessToken = () => {
    return localStorage.getItem('access_token');
}

export const setAccessToken = (token) => {
    localStorage.setItem('access_token', token);
}

export const dateTimeMillisFormatted=(dateTime)=>{
    return dayjs(dateTime).format("YYYY-MM-DD , HH:mm:ss")
}
