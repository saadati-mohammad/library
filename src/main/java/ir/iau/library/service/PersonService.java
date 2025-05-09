package ir.iau.library.service;

import ir.iau.library.entity.Person;
import ir.iau.library.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(Person person) {
        person.setActive(true);
        person.setMembershipDate(java.time.LocalDate.now());
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id " + id));
        person.setFirstName(personDetails.getFirstName());
        person.setLastName(personDetails.getLastName());
        person.setEmail(personDetails.getEmail());
        person.setPhone(personDetails.getPhone());
        person.setBirthDate(personDetails.getBirthDate());
        person.setMembershipType(personDetails.getMembershipType());
        person.setAddress(personDetails.getAddress());
        return personRepository.save(person);
    }

    public void deactivatePerson(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id " + id));
        person.setActive(false);
        personRepository.save(person);
    }

    public List<Person> findAllActive() {
        return personRepository.findAll().stream()
                .filter(Person::getActive)
                .toList();
    }
}