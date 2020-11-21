package ua.project.protester.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ua.project.protester.model.User;
import ua.project.protester.utils.UserRowMapper;

import java.util.*;

@Repository
@PropertySource("classpath:queries/user.properties")
@RequiredArgsConstructor
public class UserRepository implements CrudRepository<User> {


    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final Environment environment;
    private final UserRowMapper userRowMapper;

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private static final String PROPERTY_NOT_FOUND_TEMPLATE = "Could not find property '%s' in queries/user.properties";

    @Override
    public int save(User entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("role_id", entity.getRole().getId());
        namedParams.addValue("user_name", entity.getName());
        namedParams.addValue("user_email", entity.getEmail());
        namedParams.addValue("user_password", entity.getPassword());
        namedParams.addValue("user_active", entity.isActive());
        namedParams.addValue("user_full_name", entity.getFullName());

        int update = namedJdbcTemplate.update(environment.getProperty("saveUser"), namedParams, keyHolder, new String[]{"user_id"});

        Integer id = (Integer) (keyHolder.getKeys().get("user_id"));
        entity.setId(id.longValue());

        return update;
    }

    @Override
    public Optional<User> findById(Long id) {

        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put("user_id", id);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(environment.getProperty("findUserById"), namedParams, userRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return namedJdbcTemplate.query(environment.getProperty("findAllUsers"), userRowMapper);
    }

    @Override
    public void update(User entity) {
        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("role_id", entity.getRole().getId());
        namedParams.addValue("user_name", entity.getName());
        namedParams.addValue("user_email", entity.getEmail());
        namedParams.addValue("user_password", entity.getPassword());
        namedParams.addValue("user_active", entity.isActive());
        namedParams.addValue("user_full_name", entity.getFullName());
        namedParams.addValue("user_id", entity.getId());

        namedJdbcTemplate.update(environment.getProperty("updateUser"), namedParams);
    }

    @Override
    public void delete(User entity) {
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put("user_id", entity.getId());
        namedJdbcTemplate.update(environment.getProperty("deleteUser"), namedParams);
    }

    public List<User> findUsersByRoleId(Long roleId) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put("role_id", roleId);
            return namedJdbcTemplate.query(environment.getProperty("findUsersByRoleId"), namedParams, userRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Optional<User> findUserByEmail(String email) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put("user_email", email);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(environment.getProperty("findUserByEmail"), namedParams, userRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findUserByUsername(String username) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put("user_name", username);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(environment.getProperty("findUserByUsername"), namedParams, userRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findAllPagination(int limit, int offset) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put("limit", limit);
            namedParams.put("offset", offset);
            return namedJdbcTemplate.query(environment.getProperty("findAllUsersPagination"), namedParams, userRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }


    public Optional<String> findUserEmailByTokenValue(String tokenValue) {
        String queryPropertyName = "findUserEmailByResetPasswordTokenValue";
        try {
            String userEmail = namedJdbcTemplate.queryForObject(
                    Objects.requireNonNull(environment.getProperty("findUserEmailByResetPasswordTokenValue")),
                    new MapSqlParameterSource().addValue("value", tokenValue),
                    String.class);
            return Optional.ofNullable(userEmail);
        } catch (DataAccessException e) {
            return Optional.empty();
        } catch (NullPointerException e) {
            logger.warn(String.format(
                    PROPERTY_NOT_FOUND_TEMPLATE,
                    queryPropertyName));
            return Optional.empty();
        }
    }

    public void updatePassword(User user, String newUserPassword) {
        String queryPropertyName = "updateUserPassword";
        try {
            namedJdbcTemplate.update(
                    Objects.requireNonNull(environment.getProperty("updateUserPassword")),
                    new MapSqlParameterSource()
                            .addValue("password", newUserPassword)
                            .addValue("id", user.getId()));
        } catch (NullPointerException e) {
            logger.warn(String.format(
                    PROPERTY_NOT_FOUND_TEMPLATE,
                    queryPropertyName));
        }
    }
}
