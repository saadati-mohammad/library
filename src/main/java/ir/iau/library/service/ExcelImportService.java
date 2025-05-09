package ir.iau.library.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import ir.iau.library.dto.BookExcelRowDto;
import ir.iau.library.dto.PersonExcelRowDto;
import ir.iau.library.entity.Book;
import ir.iau.library.entity.Person;
import ir.iau.library.repository.BookRepository;
import ir.iau.library.repository.PersonRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelImportService {

    private final BookRepository bookRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ExcelImportService(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    public List<BookExcelRowDto> importBooksFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<BookExcelRowDto> bookDtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) continue;  // Skip header row

                // Read data from each cell and convert to DTO
                BookExcelRowDto bookDto = new BookExcelRowDto();
                bookDto.setIsbn10(row.getCell(0).getStringCellValue());
                bookDto.setTitle(row.getCell(1).getStringCellValue());
                bookDto.setAuthor(row.getCell(2).getStringCellValue());
                bookDto.setTranslator(row.getCell(3).getStringCellValue());
                bookDto.setDescription(row.getCell(4).getStringCellValue());
                bookDtos.add(bookDto);

                // Save to DB (optional, for large files, you might batch these operations)
                Book book = new Book(bookDto);
                bookRepository.save(book);
            }
        }

        return bookDtos;
    }

    public List<PersonExcelRowDto> importPersonsFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<PersonExcelRowDto> personDtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) continue;  // Skip header row

                // Read data from each cell and convert to DTO
                PersonExcelRowDto personDto = new PersonExcelRowDto();
                personDto.setFirstName(row.getCell(0).getStringCellValue());
                personDto.setLastName(row.getCell(1).getStringCellValue());
                personDto.setEmail(row.getCell(2).getStringCellValue());
                personDto.setPhone(row.getCell(3).getStringCellValue());
                personDtos.add(personDto);

                // Save to DB
                Person person = new Person(personDto);
                personRepository.save(person);
            }
        }

        return personDtos;
    }
}