/*
 * Copyright 2016 Edify Software Consulting.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


 package org.commonlibrary.cllo.dao.impl

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.gridfs.GridFSDBFile
import com.mongodb.gridfs.GridFSFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 3/3/14
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Profile("FS_Mongo")
class MongoContentDAO implements org.commonlibrary.cllo.dao.ContentDAO {

    @Autowired
    private GridFsOperations gridOperations

    @Override
    GridFSDBFile findByReferenceId(String referenceId) {

        long size = getNumberOfVersions(referenceId)

        GridFSDBFile result = getGridResult(referenceId, size)
        return result
    }

    @Override
    GridFSDBFile findVersionByReferenceId(String referenceId, long versionNumber) {
        GridFSDBFile result = getGridResult(referenceId, versionNumber)
        return result
    }

    @Override
    long getNumberOfVersions(String referenceId) {
        Query query = new Query()
        query.addCriteria(Criteria.where("filename").is(referenceId))

        GridFSDBFile result = gridOperations.findOne(query)

        List<GridFSDBFile> results = gridOperations.find(
                new Query().addCriteria(Criteria.where("filename").is(result.getFilename())));

        return results.size()
    }

    @Override
    List<String> getAllVersions(String referenceId) {
        Query query = new Query()
        query.addCriteria(Criteria.where("filename").is(referenceId))

        GridFSDBFile result = gridOperations.findOne(query)

        List<GridFSDBFile> results = gridOperations.find(
                new Query().addCriteria(Criteria.where("filename").is(result.getFilename())));

        List<String> versions = []
        int i = 1
        for(r in results){
            String v = ''
            v = 'v' + i
            v = v + ',' + r.getUploadDate()
            versions.add(v)
            i++
        }

        return versions
    }

    @Override
    String rollbackToVersion(String referenceId, long versionNumber) {

        GridFSDBFile result = getGridResult(referenceId, versionNumber)
        GridFSFile file = gridOperations.store(result.getInputStream(), result.getFilename(), result.getMetaData());

        return file.getFilename()

    }

    @Override
    InputStream getInputStreamByContentReferenceId(String referenceId) {

        long size = getNumberOfVersions(referenceId)
        GridFSDBFile result = getGridResult(referenceId, size)

        return result.getInputStream()

    }

    @Override
    InputStream getInputStreamByContentReferenceId(String referenceId, long versionNumber) {

        GridFSDBFile result = getGridResult(referenceId, versionNumber)
        return result.getInputStream()

    }

    @Override
    String storeContent(InputStream is, String filename, String primaryType, String secondaryType) {

        DBObject metaData = new BasicDBObject()
        metaData.put("primary-type", primaryType)
        metaData.put("secondary-type", secondaryType)

        GridFSFile file = gridOperations.store(is, filename, metaData)

        return file.getFilename()

    }

    @Override
    void deleteByReferenceId(String referenceId) {
        //Query queryFind = new Query()
        //queryFind.addCriteria(Criteria.where("filename").is(referenceId))

        //GridFSDBFile result = gridOperations.findOne(queryFind)

        //if (null != result) {
            Query queryDelete = new Query()
            queryDelete.addCriteria(Criteria.where("filename").is(referenceId/*result.getFilename()*/))

            gridOperations.delete(queryDelete)
        //}
    }

    @Override
    String getMimeType(String referenceId) {

        long size = getNumberOfVersions(referenceId)
        GridFSDBFile result = getGridResult(referenceId, size)

        return result.metaData.get("primary-type") + "/" + result.metaData.get("secondary-type")

    }

    @Override
    String getFileName(String referenceId) {

        long size = getNumberOfVersions(referenceId)
        GridFSDBFile result = getGridResult(referenceId, size)

        return result.getFilename()

    }

    @Override
    String getMD5(String referenceId) {

        long size = getNumberOfVersions(referenceId)
        GridFSDBFile result = getGridResult(referenceId, size)

        return result.getMD5()

    }

    @Override
    String getMimeType(String referenceId, long versionNumber) {

        GridFSDBFile result = getGridResult(referenceId, versionNumber)
        return result.metaData.get("primary-type") + "/" + result.metaData.get("secondary-type")

    }

    @Override
    String getFileName(String referenceId, long versionNumber) {

        GridFSDBFile result = getGridResult(referenceId, versionNumber)
        return result.getFilename()

    }

    @Override
    String getMD5(String referenceId, long versionNumber) {

        GridFSDBFile result = getGridResult(referenceId, versionNumber)
        return result.getMD5()

    }

    @Override
    String getTargetUrl(String referenceId) {
        throw new UnsupportedOperationException('Method not implemented yet')
    }

    GridFSDBFile getGridResult(String referenceId, long versionNumber){

        Query query = new Query()
        query.addCriteria(Criteria.where("filename").is(referenceId))

        GridFSDBFile result = gridOperations.findOne(query)

        List<GridFSDBFile> results = gridOperations.find(
                new Query().addCriteria(Criteria.where("filename").is(result.getFilename())));

        return results.get(versionNumber - 1 as int)

    }

}
