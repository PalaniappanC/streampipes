/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.rest.impl;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.streampipes.manager.file.FileManager;
import org.streampipes.model.client.file.FileMetadata;
import org.streampipes.rest.api.IPipelineElementFile;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/files")
public class PipelineElementFile extends AbstractRestInterface implements IPipelineElementFile {

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response storeFile(@PathParam("username") String username,
                            @FormDataParam("file_upload") InputStream uploadedInputStream,
                            @FormDataParam("file_upload") FormDataContentDisposition fileDetail) {
    try {
      FileManager.storeFile(username, fileDetail.getFileName(), uploadedInputStream);
      return ok();
    } catch (Exception e) {
      return fail();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getFileInfo() {
    List<FileMetadata> allFiles = getFileMetadataStorage().getAllFileMetadataDescriptions();
    return ok(allFiles);
  }

}