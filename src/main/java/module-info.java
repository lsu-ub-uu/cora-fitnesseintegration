module se.uu.ub.cora.fitnesseintegration {
	requires transitive se.uu.ub.cora.clientdata;
	requires transitive se.uu.ub.cora.javaclient;
	requires transitive se.uu.ub.cora.httphandler;
	requires transitive java.ws.rs;
	requires java.xml;
	requires se.uu.ub.cora.json;
	requires se.uu.ub.cora.messaging;

	exports se.uu.ub.cora.fitnesseintegration;
	exports se.uu.ub.cora.fitnesseintegration.compare;
}