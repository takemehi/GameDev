package de.htw.saarland.gamedev.nap.data.platforms;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class OneWayPlatformContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
			
	}

	@Override
	public void endContact(Contact contact) {
		contact.setEnabled(true);
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		if(fA.getUserData()!=null && fA.getUserData().equals("platformOne")){
			if(fB.getBody().getPosition().y-2<fA.getBody().getPosition().y){
				contact.setEnabled(false);
				System.out.println("true");
			}
			System.out.println("Plat: "+fA.getBody().getPosition().y+"\n"
					+"Play: "+fB.getBody().getPosition().y);
		}
		if(fB.getUserData()!=null && fB.getUserData().equals("platformOne")){
			if(fA.getBody().getPosition().y-2<fB.getBody().getPosition().y){
				contact.setEnabled(false);
				System.out.println("true");
				}
			System.out.println("Plat: "+fB.getBody().getPosition().y+"\n"
					+"Play: "+fA.getBody().getPosition().y);
		}
		
		
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
