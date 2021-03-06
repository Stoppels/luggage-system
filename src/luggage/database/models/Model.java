/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 ITopia IS102-5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package luggage.database.models;

import luggage.database.DatabaseHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import luggage.Debug;

/**
 * Model
 *
 * A super class for all the models containing default model functions
 *
 * Package: luggage.database.models
 * @author ITopia IS102-5
 */
abstract public class Model {

    /**
     *
     * @return
     */
    protected abstract String getTable();

    /**
     *
     * @return
     */
    protected abstract Model getModel();

    /**
     *
     * @return
     */
    protected abstract String getOrderBy();

    /**
     *
     */
    protected HashMap<String, String> row = new HashMap<String, String>();

    /**
     *
     */
    public Model() {

    }

    /**
     *
     * @param id
     */
    public Model(int id) {
        try {
            long startTime = System.nanoTime();

            Statement stmt = DatabaseHelper.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + getTable() + " WHERE id = " + id);
            ResultSetMetaData rsmd = rs.getMetaData();

            rs.first();

            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                String column = rsmd.getColumnName((i + 1));

                if (!column.equals("password")) {
                    row.put(column, rs.getString(column));
                }
            }

            long endTime = System.nanoTime();
            long microseconds = ((endTime - startTime) / 1000);
            Debug.print("SELECT * FROM " + getTable() + " WHERE id = " + id + " took " + microseconds + " microseconds.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     *
     * @param where
     * @param params
     */
    public Model(String where, String... params) {
        try {
            long startTime = System.nanoTime();

            PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement("SELECT * FROM " + getTable() + " WHERE " + where);

            for (int i = 0; i < params.length; i++) {
                statement.setString((i + 1), params[i]);
            }

            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            rs.first();

            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                String column = rsmd.getColumnName((i + 1));

                if (!column.equals("password")) {
                    row.put(column, rs.getString(column));
                }
            }

            long endTime = System.nanoTime();
            long microseconds = (endTime - startTime) / 1000;
            Debug.print("SELECT * FROM " + getTable() + " WHERE " + where + " took " + microseconds + " microseconds.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public List<Model> findAll() {
        return findAll("", new String[0]);
    }

    /**
     *
     * @param where
     * @param params
     * @return
     */
    public List<Model> findAll(String where, String... params) {
        try {
            long startTime = System.nanoTime();

            String query = "SELECT * FROM " + getTable();

            if (!where.equals("")) {
                query += " WHERE " + where;
            }

            if (!getOrderBy().equals("")) {
                query += " " + getOrderBy();
            }

            PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement(query);

            for (int i = 0; i < params.length; i++) {
                statement.setString((i + 1), params[i]);
            }

            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            ArrayList<Model> rowList = new ArrayList<>();

            while (rs.next()) {
                Model model = getModel();

                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String column = rsmd.getColumnName((i + 1));
                    if (!column.equals("password")) {
                        model.row.put(column, rs.getString(column));
                    }
                }

                rowList.add(model);
            }

            long endTime = System.nanoTime();
            long microseconds = (endTime - startTime) / 1000;
            Debug.print(query + " took " + microseconds + " microseconds.");

            return rowList;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    /**
     * Return the id of the current row
     *
     * @return
     */
    public int getId() {
        if (row.get("id") == null) {
            return 0;
        }

        return Integer.parseInt(row.get("id"));
    }

    /**
     * Check if the current row exists in the database
     *
     * @return boolean true if exists
     */
    public boolean exists() {
        if (getId() != 0) {
            return true;
        }

        return false;
    }

    /**
     *
     * @return create if id = 0
     */
    public boolean save() {
        if (getId() == 0) {
            return create();
        }

        return update();
    }

    /**
     *
     * @return
     */
    private boolean create() {
        try {
            long startTime = System.nanoTime();

            String sQuery = "INSERT INTO " + getTable() + " (";

            boolean firstColumn = true;
            for (Entry<String, String> column : row.entrySet()) {
                if (firstColumn) {
                    firstColumn = false;
                    sQuery = sQuery + column.getKey();
                } else {
                    sQuery = sQuery + ", " + column.getKey();
                }
            }

            sQuery = sQuery + ") VALUES (";

            firstColumn = true;
            for (Entry<String, String> column : row.entrySet()) {
                if (firstColumn) {
                    firstColumn = false;
                    sQuery = sQuery + "?";
                } else {
                    sQuery = sQuery + ", ?";
                }
            }

            sQuery = sQuery + ")";

            PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement(sQuery);

            int currentColumn = 1;
            for (Entry<String, String> column : row.entrySet()) {
                statement.setString(currentColumn, column.getValue());
                currentColumn = currentColumn + 1;
            }

            boolean result = statement.execute();

            long endTime = System.nanoTime();
            long microseconds = (endTime - startTime) / 1000;
            Debug.print(statement + " took " + microseconds + " microseconds.");

            if (!getTable().equals("log")) {
                Debug.logToDatabase(LogModel.TYPE_INFO, "User added row in " + getTable() + ".");
            }

            return result;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @return
     */

    private boolean update() {
        try {
            long startTime = System.nanoTime();

            String sQuery = "UPDATE " + getTable() + " SET ";

            boolean firstColumn = true;
            for (Entry<String, String> column : row.entrySet()) {

                if (column.getKey().equals("password") && column.getValue().equals("")) {
                    continue;
                }

                if (firstColumn) {
                    firstColumn = false;
                    sQuery = sQuery + column.getKey() + " = ?";
                } else {
                    sQuery = sQuery + ", " + column.getKey() + " = ?";
                }
            }

            sQuery = sQuery + " WHERE id = " + getId();

            PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement(sQuery);

            int currentColumn = 1;
            for (Entry<String, String> column : row.entrySet()) {
                statement.setString(currentColumn, column.getValue());
                currentColumn = currentColumn + 1;
            }

            boolean result = statement.execute();

            long endTime = System.nanoTime();
            long microseconds = (endTime - startTime) / 1000;
            Debug.print(statement + " took " + microseconds + " microseconds.");

            if (!getTable().equals("log")) {
                Debug.logToDatabase(LogModel.TYPE_INFO, "User updated row from " + getTable() + ", id: " + getId() + ".");
            }

            return result;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean delete() {
        String sQuery = "DELETE FROM " + getTable() + " WHERE id = ?";
        PreparedStatement preparedStmt;
        try {
            long startTime = System.nanoTime();

            preparedStmt = DatabaseHelper.getConnection().prepareStatement(sQuery);
            preparedStmt.setInt(1, getId());
            boolean result = preparedStmt.execute();

            long endTime = System.nanoTime();
            long microseconds = (endTime - startTime) / 1000;
            Debug.print(sQuery + " took " + microseconds + " microseconds.");

            if (!getTable().equals("log")) {
                Debug.logToDatabase(LogModel.TYPE_INFO, "User deleted row from " + getTable() + ", id: " + getId() + ".");
            }

            return result;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

}
