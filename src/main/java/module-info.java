module se.uu.ub.cora.fitnesseintegration {
	requires transitive se.uu.ub.cora.clientdata;
	requires transitive se.uu.ub.cora.javaclient;
	requires transitive se.uu.ub.cora.httphandler;
	requires transitive java.ws.rs;
	requires java.xml;
	requires se.uu.ub.cora.json;
	requires se.uu.ub.cora.messaging;
	requires se.uu.ub.cora.data;
	requires se.uu.ub.cora.converter;

	exports se.uu.ub.cora.fitnesseintegration;
	exports se.uu.ub.cora.fitnesseintegration.compare;
	exports se.uu.ub.cora.fitnesseintegration.fixture;
	exports se.uu.ub.cora.fitnesseintegration.server.compare.fixtures;
	exports se.uu.ub.cora.fitnesseintegration.apptoken.script;
	exports se.uu.ub.cora.fitnesseintegration.waiter.fixture;
}