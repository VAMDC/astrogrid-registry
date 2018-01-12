/*
 * Copyright 2018 University of Cambridge.
 *
 * This class is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.astrogrid.registry.server.query.v1_0;

/**
 * A failure to perform an XQuery. The exception message includes
 * the XQuery text.
 * 
 * @author Guy Rixon
 */
public class XQueryException extends Exception {
  
  public XQueryException(String xql, Throwable cause) {
    super("Failed to evaluate an XQuery: " + xql, cause);
  }

}
