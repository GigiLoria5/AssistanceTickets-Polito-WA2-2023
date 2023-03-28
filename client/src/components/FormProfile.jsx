import {Form, Button} from "react-bootstrap";
import {useState} from "react";
import {Profile} from "../utils/Profile";



function FormProfile(props){
    const [errorMsg,setErrorMsg] = useState('');
    const [email,setEmail] = useState('');
    const [name,setName] = useState('');
    const[surname,setSurname] = useState('');
    const[phoneNumber,setPhoneNumber] = useState('');
    const[address,setAddress] = useState('');
    const[city,setCity] = useState('');
    const[country,setCountry] = useState('');

    const handleSubmit = (event) => {
        // TODO: fare controlli sul form
        event.preventDefault();
        const newProfile= new Profile(email,name,surname,phoneNumber,address,city,country);
        props.addProfile(newProfile);

    }
    return(
        <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Email address</Form.Label>
                <Form.Control type="email" placeholder="Enter email" />
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Name</Form.Label>
                <Form.Control placeholder="Name" />
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Surname</Form.Label>
                <Form.Control placeholder="Surname" />
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Phone number</Form.Label>
                <Form.Control placeholder="Phone number" />
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Country</Form.Label>
                <Form.Control placeholder="Nation" />
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>City</Form.Label>
                <Form.Control placeholder="City" />
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Address</Form.Label>
                <Form.Control placeholder="Address" />
            </Form.Group>

            <Button  variant="primary" type="submit">
                Add
            </Button>
            <Button variant="secondary" onClick={props.changeVisible}>Cancel</Button>
        </Form>

    );
}

export default FormProfile