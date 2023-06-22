import {Alert, Button, Form} from "react-bootstrap";
import {useState} from "react";

function SearchProfile(props) {
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');

    function validateEmail(input) {
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
        return emailPattern.test(input);
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        setError('');
        if (!validateEmail(email))
            setError('Email format not valid')
        else
            props.getProfileByEmail(email);
    }

    return (
        <Form className="mt-3" onSubmit={handleSubmit}>
            {error ? <Alert variant='danger' onClose={() => setError('')} dismissible>{error}</Alert> : false}
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Search profile</Form.Label>
                <Form.Control type="email" placeholder="Enter email"
                              onChange={ev => setEmail(ev.target.value)}></Form.Control>
                <Form.Text>Insert an existing email</Form.Text>
            </Form.Group>
            <Button variant="primary" type="submit"> Search </Button>
        </Form>
    )
}

export default SearchProfile
