import {Button, Form} from "react-bootstrap";



function SearchProfile(props){
    const handleSubmit = (event) => {
        event.preventDefault();
        props.getProfile()
    }
    return(
        <Form className="mt-3" onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Search user</Form.Label>
                <Form.Control type="email" placeholder="Enter email"></Form.Control>
                <Form.Text>Insert an existing email</Form.Text>
            </Form.Group>
            <Button variant="primary" type="submit"> Search </Button>
        </Form>
    )
}
export default SearchProfile