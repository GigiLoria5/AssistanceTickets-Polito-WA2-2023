import {Button, Container, Form} from 'react-bootstrap';
import {useEffect, useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import {HttpStatusCode} from "../../enums/HttpStatusCode";
import {getAccessToken} from "../utils/utils";
import {validateEmail} from "../utils/validators";

function LoginForm() {
    const navigate = useNavigate();
    const location = useLocation();
    const success = location.state?.success;
    const {StatusAlertComponent, showSuccess, showError, resetStatusAlert} = useStatusAlert();
    const [validated, setValidated] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isUsernameValid, setIsUsernameValid] = useState(true);
    const [isPasswordValid, setIsPasswordValid] = useState(true);

    useEffect(() => {
        if (getAccessToken() !== "null") {
            navigate("/")
        }

        if (success) {
            showSuccess("Registration completed successfully")
        }
    }, [])

    const handleSubmit = async (event) => {
        event.preventDefault();
        setIsLoading(true);
        resetStatusAlert();
        const form = event.currentTarget;
        const validUsername = username && validateEmail(username);
        if (form.checkValidity() && validUsername && password) {
            const credentials = {username, password};
            await handleLogin(credentials);
        } else {
            event.stopPropagation();
        }
        setIsUsernameValid(!!validUsername);
        setIsPasswordValid(!!password);
        setValidated(true);
        setIsLoading(false);
    };

    const handleLogin = async (credentials) => {
        API.logIn(credentials)
            .then(_ => {
                navigate('/');
            })
            .catch(err => {
                showError(err.status === HttpStatusCode.UNAUTHORIZED ? "Wrong Credentials. Please try again" : err.error)
            })
    };

    return (
        <div className='color-overlay d-flex justify-content-center align-items-center min-vh-100 flex-column'>
            <Form id='login-form' className='rounded p-4 p-sm-4 bg-grey' noValidate onSubmit={handleSubmit}>
                <h2 className='text-center'>Login</h2>
                <Form.Group className='mb-3'>
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        required={true}
                        type="email"
                        placeholder="Enter email"
                        onChange={ev => setUsername(ev.target.value)}
                        value={username}
                        disabled={isLoading}
                        isValid={validated ? isUsernameValid : null}
                        isInvalid={!isUsernameValid}
                    />
                    <Form.Control.Feedback type="invalid">
                        Please provide an email.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className='mb-3'>
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                        required={true}
                        type="password"
                        placeholder="Enter password"
                        onChange={ev => setPassword(ev.target.value)}
                        value={password}
                        disabled={isLoading}
                        isValid={validated ? isPasswordValid : false}
                        isInvalid={!isPasswordValid}
                    />
                    <Form.Control.Feedback type="invalid">
                        Please provide a password.
                    </Form.Control.Feedback>
                </Form.Group>

                <Container className="p-0">
                    <div className="d-flex flex-column align-items-center mt-3">
                        <Button variant="primary" disabled={isLoading} className="w-100" type="submit">
                            {isLoading ? "In Progress..." : "Login"}
                        </Button>
                    </div>
                    <div className="d-flex flex-column align-items-center mt-3">
                        <p>
                            Don't have an account?
                            <Button
                                variant="link"
                                onClick={() => navigate("/register")}
                                className="py-0 pb-2 text-decoration-none">
                                Register
                            </Button>
                        </p>
                    </div>
                </Container>
            </Form>
            <StatusAlertComponent/>
        </div>
    )
}

export default LoginForm;
