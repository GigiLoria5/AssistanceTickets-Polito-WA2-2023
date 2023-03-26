import {Button, Form, Alert} from "react-bootstrap";
import {useState} from "react";



function SearchProfile(props){
    const [email,setEmail]=useState('');
    const [errorMessage, setErrorMessage] = useState('') ;
    function validateEmail(input){
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
        return emailPattern.test(input);
    }
    const handleSubmit = (event) => {
        event.preventDefault();
        setErrorMessage('');
        if(!validateEmail(email))
            setErrorMessage('Email format not valid')
        else
            props.getProfileByEmail(email);
    }
    return(
        <Form className="mt-3" onSubmit={handleSubmit}>
            {errorMessage ? <Alert variant='danger' onClose={() => setErrorMessage('')} dismissible >{errorMessage}</Alert> : false}
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Search user</Form.Label>
                <Form.Control type="email" placeholder="Enter email" onChange={ev => setEmail(ev.target.value)}></Form.Control>
                <Form.Text>Insert an existing email</Form.Text>
            </Form.Group>
            <Button variant="primary" type="submit"> Search </Button>
        </Form>
    )
}
export default SearchProfile