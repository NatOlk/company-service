import React, { useState }  from 'react';
import {Container, Table} from 'reactstrap';

import {gql} from 'graphql-tag';
import {useQuery} from "@apollo/client";
import DeleteAnimal from "./deleteAnimal";
import AddAnimal from "./addAnimal";
import UpdateAnimal from "./updateAnimal";

const ANIMALS_QUERY = gql`
    {
        allAnimals {
            name
            species
            primary_color
            breed
            implant_chip_id
            gender
            birth_date
            pattern
        }}
`;

function AnimalsList() {

    const perPage = 10;
    const [currentPage, setCurrentPage] = useState(0);
    const {loading, error, data} = useQuery(ANIMALS_QUERY);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error :(</p>;

    const pageCount = Math.ceil(data.allAnimals.length / perPage);

    const animalsList = data.allAnimals
        .slice(currentPage * perPage, (currentPage + 1) * perPage)
        .map(animal => <UpdateAnimal key={animal.name + animal.species} animal={animal} />);

    return (
        <div>
            <div id="error" className="errorAlarm"></div>

            <Container fluid>
                <h3>Animals</h3>

                <Table>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Species</th>
                        <th>Primary color</th>
                        <th>Breed</th>
                        <th>Implant chip id</th>
                        <th>Gender</th>
                        <th>Birth date</th>
                        <th>Pattern</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                     <AddAnimal/>
                      {animalsList}
                     </tbody>
                     </Table>
                    <div>
                        {
                         Array.from({length: pageCount}, (_, index) => (
                           <button key={index} className="round-button-with-border"
                              onClick={() => setCurrentPage(index)}>{index + 1}</button>
                         ))
                         }
                    </div>
            </Container>
        </div>
    );
}

export default AnimalsList;
