const validator = require("email-validator");

export function validateEmail(input) {
    return validator.validate(input);
}

export function validateName(input) {
    const namePattern = /([A-Za-z][a-z]*)+([ '\\-][A-Za-z]+)*[/.']?$/;
    return namePattern.test(input);
}

export function validatePhone(input) {
    const phonePattern = /(\d{10})/;
    return phonePattern.test(input);
}

export function validateAddress(input) {
    const addressPatten = /^[0-9A-Za-z]+([^0-9A-Za-z]{0,2}[a-zA-Z0-9]+)*$/;
    return addressPatten.test(input);
}

export function validateCity(input) {
    const cityPattern = /[a-zA-Z]+([ \\-][a-zA-Z]+)*$/;
    return cityPattern.test(input);
}

export function validateCountry(input) {
    const countryPattern = /[a-zA-Z]+( [a-zA-Z]+)*$/;
    return countryPattern.test(input);
}
