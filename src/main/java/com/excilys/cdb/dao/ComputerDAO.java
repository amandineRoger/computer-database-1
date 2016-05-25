package com.excilys.cdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.jdbc.ConnectionManager;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.mapper.LocalDateMapper;
import com.excilys.cdb.mapper.MapperException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.PageParameters;
import com.excilys.cdb.model.PageParameters.Order;

/**
 * Singleton for the ComputerDAO.
 *
 * implements all the CRUD operations defined in DAO<>.
 *
 * @author simon
 *
 */
@Repository
public class ComputerDAO implements DAO<Computer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDAO.class);

    @Autowired
    private ComputerMapper mapper;

    @Autowired
    private LocalDateMapper dateMapper;

    private static final String FIND_BY_ID = "SELECT c.id, c.name, c.introduced, c.discontinued, c.company_id, o.name as company_name FROM computer c LEFT JOIN company o on c.company_id=o.id WHERE c.id=?";

    private static final String CREATE = "INSERT INTO computer (name, introduced, discontinued, company_id) VALUES(?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE computer SET name=?, introduced=?, discontinued=?, company_id=? WHERE id=?";

    private static final String DELETE = "DELETE FROM computer WHERE id=?";

    private static final String DELETE_LIST = "DELETE FROM computer WHERE id IN (%s)";

    private static final String FIND_ALL = "SELECT c.id, c.name, c.introduced, c.discontinued, c.company_id, o.name as company_name FROM computer c LEFT JOIN company o ON c.company_id=o.id";

    private static final String FIND_ALL_BETTER = "SELECT B.id, B.name, B.introduced, B.discontinued, B.company_id, C.name as company_name FROM (SELECT id FROM computer WHERE name like ? ORDER BY %s %s LIMIT ?, ?) A LEFT JOIN computer B on B.id=A.id LEFT JOIN company C on B.company_id=C.id";

    private static final String FIND_ALL_LIMIT_ORDER = "SELECT c.id, c.name, c.introduced, c.discontinued, c.company_id, o.name as company_name FROM computer c %s left join company o ON c.company_id=o.id WHERE c.name like ? ORDER BY %s %s LIMIT ?,?";

    private static final String FIND_ALL_BETTER_NO_SEARCH = "SELECT B.id, B.name, B.introduced, B.discontinued, B.company_id, C.name as company_name FROM (SELECT id FROM computer ORDER BY %s %s LIMIT ?, ?) A LEFT JOIN computer B on B.id=A.id LEFT JOIN company C on B.company_id=C.id";

    private static final String COUNT = "SELECT count(id) as nb FROM computer";

    private static final String COUNT_SEARCH = "SELECT count(name) as nb FROM computer WHERE name like ?";

    private static final String DELETE_COMPUTER = "DELETE FROM computer WHERE company_id=?";

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "HikariDatasource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Computer find(Long id) {

        Computer computer = null;

        try {
            computer = this.jdbcTemplate.queryForObject(FIND_BY_ID, (rs1, rowNum) -> ComputerDAO.this.mapper.map(rs1), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }

        return computer;
    }

    @Override
    public Computer create(Computer obj) {

        try {

            Timestamp introduced = this.dateMapper.toTimestamp(obj.getIntroduced());

            Timestamp discontinued = this.dateMapper.toTimestamp(obj.getDiscontinued());

            Long companyId = obj.getCompany() == null ? null : obj.getCompany().getId();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int res = this.jdbcTemplate.update((PreparedStatementCreator) connection -> {
                PreparedStatement ps = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
                this.setParams(ps, obj.getName(), introduced, discontinued, companyId);
                return ps;
            }, keyHolder);

            if (res > 0) {
                obj.setId(keyHolder.getKey().longValue());
                LOGGER.info("successfully created computer : " + obj.toString());
            } else {
                LOGGER.warn("Could not create computer : " + obj.toString());
                throw new DAOException("Could not create computer.");
            }

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }

        return obj;
    }

    @Override
    public Computer update(Computer obj) {

        try {

            Timestamp introduced = this.dateMapper.toTimestamp(obj.getIntroduced());

            Timestamp discontinued = this.dateMapper.toTimestamp(obj.getDiscontinued());

            Long companyId = obj.getCompany() == null ? null : obj.getCompany().getId();

            int res = this.jdbcTemplate.update(UPDATE, new Object[] { obj.getName(), introduced, discontinued, companyId, obj.getId() });

            if (res > 0) {
                LOGGER.info("Successfully updated computer : " + obj.toString());
            } else {
                LOGGER.warn("Could not update computer : " + obj.toString());
            }

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }

        return obj;
    }

    @Override
    public void delete(Computer obj) {
        try {
            int res = this.jdbcTemplate.update(DELETE, obj.getId());

            if (res > 0) {
                ComputerDAO.LOGGER.info("successfully deleted computer : " + obj.toString());
            } else {
                ComputerDAO.LOGGER.warn("couldn't delete computer : " + obj.toString());
            }
        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }
    }

    /**
     * Delete computers based on their company.
     *
     * @param id
     *            id of the company to whom the computers to delete belong.
     */
    public void deleteByCompanyId(Long id) {

        try {

            this.jdbcTemplate.update(DELETE_COMPUTER, id);

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteAll(List<Long> objs) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < objs.size(); i++) {
            builder.append(objs.get(i) + ",");
        }

        String s = String.format(DELETE_LIST, builder.deleteCharAt(builder.length() - 1).toString());

        try {

            this.jdbcTemplate.update(s);

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }
    }

    @Override
    public List<Computer> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Computer> findAll(PageParameters page) {

        List<Computer> result = null;

        try {

            String search = page.getSearch() == null ? "" : page.getSearch();

            String forceIndex = "";

            if (page.getOrder() == Order.NAME) {
                forceIndex = "force index (ix_name)";
            } else if (page.getOrder() == Order.DISCONTINUED) {
                forceIndex = "force index (ix_discontinued)";
            } else if (page.getOrder() == Order.INTRODUCED) {
                forceIndex = "force index (ix_introduced)";
            }

            String str = String.format(FIND_ALL_LIMIT_ORDER, forceIndex, page.getOrder().toString(), page.getDirection().toString());

            result = this.jdbcTemplate.query(str, (rs, rowNum) -> ComputerDAO.this.mapper.map(rs), new Object[] { search + "%", page.getSize() * page.getPageNumber(), page.getSize() });

            if (result.size() > 0) {
                ComputerDAO.LOGGER.info("successfully retrieved " + result.size() + " computer(s)");
            } else {
                ComputerDAO.LOGGER.warn("couldn't retrieve any computers");
            }

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }

        return result;
    }

    /**
     * Count number of computers using a page parameters.
     *
     * @param page
     *            parameters for the query.
     * @return number of computers.
     */
    public long count(PageParameters page) {

        long nb = 0;

        try {

            nb = this.jdbcTemplate.queryForObject(COUNT_SEARCH, Long.class, page.getSearch() + "%");

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }

        return nb;
    }

    @Override
    public long count() {

        long nb = 0;

        try {

            nb = this.jdbcTemplate.queryForObject(COUNT, Long.class);

        } catch (DataAccessException e) {
            ComputerDAO.LOGGER.error(e.getMessage());
            throw new DAOException(e);
        }

        return nb;
    }
}
