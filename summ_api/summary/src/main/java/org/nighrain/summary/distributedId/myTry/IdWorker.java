package org.nighrain.summary.distributedId.myTry;

import java.util.Map;

/**
 *    
 * Title         [title]
 * Author:       [nighrian]
 * CreateDate:   [2019-04-21--17:54]  @_@ ~~
 * Version:      [v1.0]
 * Description:  [..]
 * <p></p>
 *  
 */
public interface IdWorker {
   long nextId();
   Map<String,Object> parseId(long id);
}
