package org.basex.api.webdav;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.Open;
import org.basex.util.StringList;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;

/**
 * WebDAV resource factory. Main class for generating WebDAV resources.
 * @author BaseX Team 2005-11, BSD License
 * @author Rositsa Shadura
 * @author Dimitar Popov
 */
public class BXResourceFactory implements ResourceFactory {
  /** Database context. */
  private Context ctx;

  /**
   * Constructor.
   * @param c database context
   */
  public BXResourceFactory(final Context c) {
    ctx = c;
  }

  @Override
  public Resource getResource(final String host, final String p) {
    final Path path = Path.path(p);

    // root
    if(path.isRoot()) return new BXAllDatabasesResource(ctx);

    final String[] steps = path.getParts();

    // database
    final boolean dbexists = listDatabases(ctx).contains(steps[0]);
    if(!dbexists) return null;

    // open db

    if(steps.length == 1) {
      final String db = steps[0];
      return isCollection(ctx, db) ?
          new BXCollectionDatabaseResource(ctx, db) :
          new BXDocumentResource(ctx, db);
    }

    // TODO collection
    return null;
  }

  /**
   * List all databases.
   * @param ctx context
   * @return a list of database names
   */
  static StringList listDatabases(final Context ctx) {
    return org.basex.core.cmd.List.list(ctx);
  }

  /**
   * Is the specified database a collection of documents?
   * @param ctx context
   * @param db database name
   * @return <code>true</code> if the database has more than one document
   */
  static boolean isCollection(final Context ctx, final String db) {
    try {
      new Open(db).execute(ctx);
      final boolean result = ctx.data.meta.ndocs > 1;
      new Close().execute(ctx);
      return result;
    } catch(BaseXException e) {
      // [DP] WebDAV: error handling
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Get a valid database name from a general file name.
   * @param n name
   * @return valid database name
   */
  static String dbname(final String n) {
    final int i = n.lastIndexOf(".");
    return (i != -1 ? n.substring(0, i) : n).replaceAll("[^\\w-]", "");
  }
}
