import {Button, Container, Form} from 'react-bootstrap';
import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import {HttpStatusCode} from "../../enums/HttpStatusCode";

const validator = require("email-validator");

function LoginForm() {
    const navigate = useNavigate();
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [validated, setValidated] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isUsernameValid, setIsUsernameValid] = useState(true);
    const [isPasswordValid, setIsPasswordValid] = useState(true);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setIsLoading(true);
        resetStatusAlert();
        const form = event.currentTarget;
        const validUsername = username && validator.validate(username);
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
                <h2 className='text-center'>Login Form</h2>
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

                <Container className="d-flex justify-content-between p-0">
                    <Button variant="primary" disabled={isLoading} className="mb-1 ml-5" type="submit">
                        {isLoading ? "In progress..." : "Login"}
                    </Button>
                </Container>
            </Form>
            <StatusAlertComponent/>
        </div>
    )
}

export default LoginForm;
