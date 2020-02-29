package datadog.trace.instrumentation.jdbc;

import datadog.trace.agent.tooling.WeakCache;
import datadog.trace.bootstrap.instrumentation.jdbc.DBInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * JDBC instrumentation shares a global map of connection info.
 *
 * <p>Should be injected into the bootstrap classpath.
 */
public class JDBCMaps {
  public static final WeakCache<Connection, DBInfo> connectionInfo = WeakCache.newWeakCache();
  public static final WeakCache<PreparedStatement, String> preparedStatements =
      WeakCache.newWeakCache();
}
