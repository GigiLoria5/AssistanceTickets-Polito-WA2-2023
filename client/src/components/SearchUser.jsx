import {Button, Form} from "react-bootstrap";


function SarchUser(props){
    return(
        <Form>
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Email address</Form.Label>
                <Form.Control type="email" placeholder="Enter email"></Form.Control>
                <Form.Text>Insert an existing email</Form.Text>
            </Form.Group>
            <Button variant="primary" type="submit"> Search </Button>
        </Form>
    )
}
export default SarchUser