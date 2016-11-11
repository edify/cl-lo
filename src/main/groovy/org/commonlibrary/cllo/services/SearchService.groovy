package org.commonlibrary.cllo.services
/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 11/6/13
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */

interface SearchService {

    List<org.commonlibrary.cllo.model.LearningObject> search(String query, List<String> inclusions, List<String> exclusions, int searchFrom, int searchSize, Locale locale)

    List<String> suggestAltTerms(String query, Locale locale)

    List<org.commonlibrary.cllo.model.LearningObject> searchMoreLikeThis(String id, int searchFrom, int searchSize, Locale locale)

}
