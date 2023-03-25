import {Button, Card} from "react-bootstrap";

function DisplayProfile(props){
    const {email,name,surname,number}= props.profile;
    return (
        <Card className="mt-3" >
            <Card.Body >
                <Card.Title>{name} {surname}</Card.Title>
                <Card.Text>{email}</Card.Text>
                <Card.Text>{number}</Card.Text>
                <Button  variant="primary">Edit</Button>
            </Card.Body>

        </Card>
    )
}

export default DisplayProfile