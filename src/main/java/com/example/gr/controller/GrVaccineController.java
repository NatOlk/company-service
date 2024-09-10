package com.example.gr.controller;

import com.example.gr.jpa.data.Animal;
import com.example.gr.jpa.data.Vaccination;
import com.example.gr.service.VaccinationService;
import com.example.gr.service.exception.*;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class GrVaccineController
{
	private static final Logger LOG = LoggerFactory.getLogger(GrVaccineController.class);

	@Autowired
	private VaccinationService vaccinationService;

	@MutationMapping
	public Vaccination updateVaccination(@Argument Long id,
									@Argument String vaccine, @Argument String batch,
									@Argument String vaccination_time, @Argument String comments,
							        @Argument String email) throws VaccinationNotFoundException, VaccinationUpdateException {
		return vaccinationService.updateVaccination(id, vaccine, batch, vaccination_time,
				comments, email);
	}

	@MutationMapping
	public Vaccination deleteVaccination(@Argument Long id) throws VaccinationNotFoundException {
		//TODO fix it
		Vaccination vaccination = vaccinationService.findByKey(id)
				.orElseThrow(() -> new VaccinationNotFoundException("Vaccination not found for: " + id));
		vaccinationService.deleteVaccination(id);
		return vaccination;
	}

	@QueryMapping
	public List<Vaccination> allVaccinations()
	{
		return vaccinationService.getAllVaccinations();
	}

	@QueryMapping
	public List<Vaccination> vaccinationByAnimalId(@Argument Long animalId)
	{
		return vaccinationService.findByAnimalId(animalId);
	}


	@QueryMapping
	public int vaccinationCountById(@Argument Long id)
	{
		return vaccinationService.vaccinationCountById(id);
	}

	@MutationMapping
	public Vaccination addVaccination(@Argument Long animalId, @Argument String vaccine,
									  @Argument String batch, @Argument String vaccination_time,
									  @Argument String comments, @Argument String email) throws VaccinationCreationException {
		return vaccinationService.addVaccination(animalId, vaccine, batch, vaccination_time, comments, email);
	}


	@SchemaMapping(typeName="Animal", field="vaccinations")
	public List<Vaccination> getVaccinations(Animal animal) {
		return vaccinationService.findByAnimalId(animal.getId());
	}

	@SchemaMapping
	public int vaccinationCount(Animal animal) {
		return vaccinationService.vaccinationCountById(animal.getId());
	}

	@GraphQlExceptionHandler
	public GraphQLError handle(@NonNull Throwable ex, @NonNull DataFetchingEnvironment environment){
		return GraphQLError
				.newError()
				.errorType(ErrorType.BAD_REQUEST)
				.message(ex.getMessage())
				.path(environment.getExecutionStepInfo().getPath())
				.location(environment.getField().getSourceLocation())
				.build();
	}
}
