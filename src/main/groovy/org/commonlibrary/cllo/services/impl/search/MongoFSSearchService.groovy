package org.commonlibrary.cllo.services.impl.search

import org.apache.tika.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.stereotype.Service

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 3/5/14
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated Common Library currently supports only ElasticSearch based searches.
 */

@Deprecated
@Service
@Profile('SRCH_Mongo')
class MongoFSSearchService implements org.commonlibrary.cllo.services.SearchService {

    @Autowired
    private org.commonlibrary.cllo.repositories.LearningObjectRepository learningObjectRepository

    @Autowired
    private GridFsOperations gridOperations

    public MongoFSSearchService(org.commonlibrary.cllo.repositories.LearningObjectRepository lo, GridFsOperations gO) {

        learningObjectRepository = lo
        gridOperations = gO

    }

    List<org.commonlibrary.cllo.model.LearningObject> search(String query) {
        try {

            String[] splitQuery = query.split('\\+')


            List<org.commonlibrary.cllo.model.LearningObject> los = []
            List<org.commonlibrary.cllo.model.LearningObject> allLO = learningObjectRepository.findAll(true)

            for (lo in allLO) {


                boolean filtersResult = true

                if (splitQuery.size() > 1) {
                    List<String> toFilter = []
                    for (int i = 1; i < splitQuery.size(); i++) {
                        toFilter.add(splitQuery[i])
                    }
                    filtersResult = applyPhysicalFilters(toFilter, lo)

                }

                if (filtersResult) {
                    boolean queryResult = matchExactQuery(splitQuery[0], lo)
                    if (queryResult) {
                        los.add(lo)
                    }
                }
            }

            return los

        }
        catch (Exception e) {
            return null
        }

    }

    private boolean applyPhysicalFilters(List<String> filters, org.commonlibrary.cllo.model.LearningObject lo) {

        boolean apply = true
        for (f in filters) {
            String[] splitFilter = f.split(':')
            apply = applyFilter(splitFilter[0], splitFilter[1], lo)
            if (apply) {
                break
            }
        }

        return apply
    }

    private boolean applyFilter(String key, String value, org.commonlibrary.cllo.model.LearningObject lo) {

        boolean result = true

        switch (key) {
            case 'format':
                result = (null != lo.getFormat() && lo.getFormat().name().toLowerCase() == value.toLowerCase())
                break
            case 'type':
                result = (null != lo.getType() && lo.getType().name().toLowerCase() == value.toLowerCase())
                break
            default:
                result = false
                break
        }

        return result
    }

    private boolean matchExactQuery(String query, org.commonlibrary.cllo.model.LearningObject lo) {
        if (matchLOSimpleProperties(query, lo) || matchLOMetadataProperties(query, lo.getMetadata()) || matchContents(lo.getFormat(), lo.getContents(), query)) {
            return true
        } else {
            return false
        }
    }

    private boolean matchLOSimpleProperties(String query, org.commonlibrary.cllo.model.LearningObject lo) {
        if (matchQuery(lo.getTitle(), query) || matchQuery(lo.getName(), query)
                || matchQuery(lo.getSubject(), query) || matchQuery(lo.getDescription(), query)) {
            return true
        } else {
            return false
        }
    }

    //TODO:Comparar por el price del LO
    private boolean matchLOMetadataProperties(String query, org.commonlibrary.cllo.model.Metadata metadata) {
        if (null != metadata) {
            if (matchQuery(metadata.getKeywords(), query) || matchQuery(metadata.getAuthor(), query)
                    || matchQuery(metadata.getCoverage(), query) || matchQuery(metadata.getIsbn(), query)
                    || matchQuery(metadata.getTopic(), query) || matchExtraMetadata(metadata.getExtraMetadata(), query)
                    || matchEnumMetadata(metadata, query)) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }

    }

    private boolean matchExtraMetadata(List<String> extrametadata, String query) {
        if (null != extrametadata) {
            boolean result = false
            for (em in extrametadata) {
                result = matchQuery(em, query)
                if (result) {
                    break
                }
            }
            return result
        } else {
            return false
        }


    }

    private boolean matchEnumMetadata(org.commonlibrary.cllo.model.Metadata metadata, String query) {

        if ((null != metadata.getDifficulty() && matchQuery(metadata.getDifficulty().name(), query))
                || (null != metadata.getInteractivityDegree() && matchQuery(metadata.getInteractivityDegree().name(), query))
                || (null != metadata.getContext() && matchQuery(metadata.getContext().name(), query))
                || (null != metadata.getEndUser() && matchQuery(metadata.getEndUser().name(), query))
                || (null != metadata.getLanguage() && matchQuery(metadata.getLanguage().name(), query))
                || (null != metadata.getStatus() && matchQuery(metadata.getStatus().name(), query))) {
            return true
        } else {
            return false
        }
    }

    private boolean matchQuery(String t, String q) {
        if (null != t) {
            if (t.toLowerCase().contains(q.toLowerCase())) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }

    private boolean matchContents(org.commonlibrary.cllo.model.metadatavalues.Format f, org.commonlibrary.cllo.model.Contents contents, String query) {

        if (null != f && f.name().toLowerCase() != 'url') {
            String[] urlParts = contents.getUrl().split("/")
            if (urlParts.size() < 5) {
                String id = urlParts[4]

                org.commonlibrary.cllo.dao.impl.MongoContentDAO cd = new org.commonlibrary.cllo.dao.impl.MongoContentDAO(gridOperations)

                InputStream result = cd.getInputStreamByContentReferenceId(id)
                String decoded = new String(IOUtils.toByteArray(result), "UTF-8");

                if (decoded.toLowerCase().contains(query.toLowerCase())) {
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            return false
        }


    }

    @Override
    List<org.commonlibrary.cllo.model.LearningObject> search(String query, List<String> inclusions, List<String> exclusions, int searchFrom, int searchSize, Locale locale) {
        throw new UnsupportedOperationException('Method not implemented yet')
    }

    @Override
    List<String> suggestAltTerms(String query, Locale locale) {
        throw new UnsupportedOperationException('Method not implemented yet')
    }

    @Override
    List<org.commonlibrary.cllo.model.LearningObject> searchMoreLikeThis(String id, int searchFrom, int searchSize, Locale locale) {
        throw new UnsupportedOperationException('Method not implemented yet')
    }
}