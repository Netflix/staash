/*******************************************************************************
 * /*
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 *  *
 ******************************************************************************/
package com.netflix.staash.rest.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.netflix.staash.json.JsonArray;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.util.StaashConstants;
import com.netflix.staash.service.DataService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/data")
public class PaasDataResourceImplNew {
	private DataService datasvc;

	@Inject
	public PaasDataResourceImplNew(DataService data) {
		this.datasvc = data;
	}
	
	@GET
	@Path("{db}/{table}")
	@Produces(MediaType.APPLICATION_JSON)
	public String listAllRow(@PathParam("db") String db,
			@PathParam("table") String table) {
		return datasvc.listRow(db, table, "", "");
	}

	@GET
	@Path("{db}/{table}/{keycol}/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public String listRow(@PathParam("db") String db,
			@PathParam("table") String table,
			@PathParam("keycol") String keycol, @PathParam("key") String key) {
		return datasvc.listRow(db, table, keycol, key);
	}

	@GET
	@Path("/join/{db}/{table1}/{table2}/{joincol}/{value}")
	@Produces(MediaType.APPLICATION_JSON)
	public String doJoin(@PathParam("db") String db,
			@PathParam("table1") String table1,
			@PathParam("table2") String table2,
			@PathParam("joincol") String joincol,
			@PathParam("value") String value) {
		return datasvc.doJoin(db, table1, table2, joincol, value);
	}

	@GET
	@Path("/timeseries/{db}/{table}/{eventtime}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEvent(@PathParam("db") String db,
			@PathParam("table") String table,
			@PathParam("eventtime") String time) {
		String out;
		try {
			out = datasvc.readEvent(db, table, time);
		} catch (RuntimeException e) {
			out = "{\"msg\":\"" + e.getMessage() + "\"}";
		}
		return out;
	}

	@GET
	@Path("/timeseries/{db}/{table}/{prefix}/{eventtime}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEvent(@PathParam("db") String db,
			@PathParam("table") String table,
			@PathParam("prefix") String prefix,
			@PathParam("eventtime") String time) {
		String out;
		try {
			out = datasvc.readEvent(db, table, prefix, time);
		} catch (RuntimeException e) {
			out = "{\"msg\":\"" + e.getMessage() + "\"}";
		}
		return out;
	}

	@GET
	@Path("/timeseries/{db}/{table}/{prefix}/{starttime}/{endtime}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEvent(@PathParam("db") String db,
			@PathParam("table") String table,
			@PathParam("prefix") String prefix,
			@PathParam("starttime") String starttime,
			@PathParam("endtime") String endtime) {
		String out;
		try {
			out = datasvc.readEvent(db, table, prefix, starttime, endtime);
		} catch (RuntimeException e) {
			out = "{\"msg\":\"" + e.getMessage() + "\"}";
		}
		return out;
	}

	@POST
	@Path("{db}/{table}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateRow(@PathParam("db") String db,
			@PathParam("table") String table, String rowObject) {
		return datasvc.writeRow(db, table, new JsonObject(rowObject));
	}

	@POST
	@Path("/timeseries/{db}/{table}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertEvent(@PathParam("db") String db,
			@PathParam("table") String table, String rowStr) {
		JsonArray eventsArr = new JsonArray(rowStr);
		return datasvc.writeEvents(db, table, eventsArr);
	}

	@GET
	@Path("/kvstore/{key}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] getObject(@PathParam("key") String key) {
		byte[] value = datasvc.fetchValueForKey("kvstore", "kvmap", "key", key);
		return value;

	}
	@POST
	@Path("/kvstore")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String storeFile(
			@FormDataParam("value") InputStream uploadedInputStream,
			@FormDataParam("value") FormDataContentDisposition fileDetail) {
		writeToKVStore(uploadedInputStream, fileDetail.getFileName());
		return "success";

	}

	private void writeToKVStore(InputStream uploadedInputStream,
			String uploadedFileName) {

		try {
			String uploadedFileLocation = "/tmp/" + uploadedFileName;
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			byte[] fbytes = Files.toByteArray(new File(uploadedFileLocation));
			if (fbytes!=null && fbytes.length>StaashConstants.MAX_FILE_UPLOAD_SIZE_IN_KB*1000000) {
				throw new RuntimeException("File is too large to upload, max size supported is 2MB");
			}
			JsonObject obj = new JsonObject();
			obj.putString("key", uploadedFileName);
			obj.putBinary("value", fbytes);
			datasvc.writeToKVStore("kvstore", "kvmap", obj);

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} 
	}
}
