package com.excilys.cdb.main;

import java.time.LocalDate;
import java.util.ArrayList;

import com.excilys.cdb.dao.CompanyDAO;
import com.excilys.cdb.dao.ComputerDAO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

/**
 * @author excilys
 */
public class MainApp {
	
	private ComputerDAO computerDAO;
	
	private CompanyDAO companyDAO;
	
	public MainApp() {
		computerDAO = new ComputerDAO();
		companyDAO = new CompanyDAO();
	}
	
	/**
	 * delete a computer
	 * 
	 * @param id id of the computer to delete
	 */
	public void deleteComputer(Long id) {
		
		// we get the computer to see if it exists
		Computer computer = this.computerDAO.find(id);

		if (computer != null) {
			this.computerDAO.delete(computer);
		}
	}
	
	/**
	 * update a computer
	 * 
	 * @param id id of the computer
	 * @param name new name
	 * @param introduced new introduced date
	 * @param discontinued new discontinued date
	 * @param companyId new company id (0 if no company)
	 */
	public void updateComputer(Long id, String name, LocalDate introduced, LocalDate discontinued, Long companyId) {

		// use a default company if id <= 0
		Company company = companyId <= 0 ? new Company() : this.companyDAO.find(companyId);

		Computer computer = new Computer(id, name, introduced, discontinued, company);

		this.computerDAO.update(computer);

	}
	
	/**
	 * create a new computer
	 * 
	 * @param name name of the computer
	 * @param introduced introduced date
	 * @param discontinued discontinued date
	 * @param companyId id of the company (0 if no company)
	 */
	public void createComputer(String name, LocalDate introduced, LocalDate discontinued, Long companyId) {

		// use a default company if id <= 0
		Company company = companyId <= 0 ? new Company() : this.companyDAO.find(companyId);

		Computer computer = new Computer(0L, name, introduced, discontinued, company);

		this.computerDAO.create(computer);

	}
	
	/**
	 * get company by its id
	 * 
	 * @param id
	 * @return company or null if invalid id or computer doesn't exist
	 */
	public Company getCompany(Long id) {
		if (id != null && id > 0) {
			return this.companyDAO.find(id);
		} else {
			return null;
		}
	}
	
	/**
	 * get computer by its id
	 * 
	 * @param id
	 * @return computer or null if invalid id or computer doesn't exist
	 */
	public Computer getComputer(Long id) {
		if (id != null && id > 0) {
			return this.computerDAO.find(id);
		} else {
			return null;
		}
	}
	
	/**
	 * get list of computers
	 * 
	 * @param offset
	 * @param nb
	 * @return the list of computers
	 */
	public ArrayList<Computer> getComputers(int offset, int nb) {
		if (offset >= 0 && nb > 0) {
			return this.computerDAO.findAll(offset, nb);
		} else {
			return new ArrayList<Computer>();
		}
	}
	
	/**
	 * get list of company
	 * 
	 * @param start
	 * @param nb 
	 * @return the list of company
	 */
	public ArrayList<Company> getCompanies(int offset, int nb) {
		if (offset >= 0 && nb > 0) {
			return this.companyDAO.findAll(offset, nb);
		} else {
			return new ArrayList<Company>();
		}
	}
}
