package com.ryce.frugalist;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(
	name = "frugalist",
	version = "v1",
	namespace = @ApiNamespace(
			ownerDomain = "frugalist.ryce.com",
	        ownerName = "RYCE",
	        packagePath=""
	),
	title = "Frugalist API",
	description = "FrugaList API",
	canonicalName = "FrugaList",
	transformers = {} // used to transform 1 object into another (useful for formatting json)
)
public class FreebieApi {

}
