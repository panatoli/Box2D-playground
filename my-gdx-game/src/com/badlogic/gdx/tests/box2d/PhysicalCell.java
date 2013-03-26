package com.badlogic.gdx.tests.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.World;


class PhysicalCell {
	
	World world;
	Body bodies[][];
	Texture texture;
	Mesh mesh;
	private Vector2[][] buttomLeft;
	static private Vector2 textureSize;
	private static Vector2 physicalSize;

	
	public PhysicalCell(Vector2 pos, Texture texture, World world) {

		// create 3 x 3 mesh

		this.world = world;
		this.texture = texture;
		bodies = new Body[3][3];

		FixtureDef fd = new FixtureDef();
		fd = new FixtureDef();
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(physicalSize.x / 6, physicalSize.y / 6);
		//CircleShape shape = new CircleShape();
		//shape.setRadius(r);
		
		fd.shape = shape;
		fd.density = 0.2f;
		fd.friction = 0.0f;
		fd.restitution = 0.0f;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;

		// body vertices
		float x = pos.x;
		float y = pos.y;
		float dx = physicalSize.x / 3;
		float dy = physicalSize.y / 3;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i * j == 1)
					continue;
				bd.position.set(x + i * dx, y + j * dy);
				Body body = world.createBody(bd);
				body.createFixture(fd);
				bodies[i][j]= body;
			}
		}
		// central body
		shape.setAsBox(0, 0);
		bodies[1][1] = world.createBody(bd);
		shape.dispose();
		
		// edges
		
		int n = 3;
		float dr = (float) Math.sqrt(dx * dx + dy * dy);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Body body = bodies[i][j];
				Body body2;
				if (i < n - 1) {
					body2 = bodies[i+1][j];
					join(body, body2, dx);
				}
				
				if (j < n - 1) {
					body2 = bodies[i][j+1];
					join(body, body2, dy);
				}
				
				if (i < n - 1 && j < n - 1) {
					body2 = bodies[i + 1][j + 1];
					join(body, body2, dr);
				}
				
				if (i < n - 1 && j > 0) {
					body2 = bodies[i + 1][j - 1];
					join(body, body2, dr);
				}					
			}
		}
	} // constructor
	
	
	void setTextureBL(Vector2 bl[][]) { 
		this.buttomLeft = bl; // 2 x 2 vector of bottom left texture coords for 1/4 cell
	}

	static public void SetSize(Vector2 textureSize, Vector2 physicalSize) {
		PhysicalCell.textureSize = textureSize; // size of each 1/4 cell texture
		PhysicalCell.physicalSize = physicalSize; 
	}
	
	void buildMesh() { 

		mesh = new Mesh(true, 4 * 4 * 4, 3 * 2 * 2 * 2, 
	            new VertexAttribute(Usage.Position, 2, "a_position"),
	            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
	
		float vertices[] = new float[4 * 4 * 4];
		short indices[] = new short[3 * 2 * 2 * 2];
	
		Vector2 pos, tex;
		int v = 0;
		int ind = 0;
		
		// 4 x 1/4 cell
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				tex = buttomLeft[i][j];
				
				pos = bodies[i][j].getPosition();
				vertices[4 * (v + 0) + 0] = pos.x - (1 - i) * physicalSize.x / 6; 
				vertices[4 * (v + 0) + 1] = pos.y - (1 - j) * physicalSize.y / 6;
				vertices[4 * (v + 0) + 2] = tex.x; 
				vertices[4 * (v + 0) + 3] = tex.y;
				
				pos = bodies[i + 1][j].getPosition();
				vertices[4 * (v + 1) + 0] = pos.x - (0 - i) * physicalSize.x / 6;
				vertices[4 * (v + 1) + 1] = pos.y - (1 - j) * physicalSize.y / 6;
				vertices[4 * (v + 1) + 2] = tex.x + textureSize.x; 
				vertices[4 * (v + 1) + 3] = tex.y;
	
				pos = bodies[i + 1][j + 1].getPosition();
				vertices[4 * (v + 2) + 0] = pos.x - (0 - i) * physicalSize.x / 6;
				vertices[4 * (v + 2) + 1] = pos.y - (0 - j) * physicalSize.y / 6;
				vertices[4 * (v + 2) + 2] = tex.x + textureSize.x; 
				vertices[4 * (v + 2) + 3] = tex.y - textureSize.y; // rrrr!!					
				
				pos = bodies[i][j + 1].getPosition();
				vertices[4 * (v + 3) + 0] = pos.x - (1 - i) * physicalSize.x / 6;
				vertices[4 * (v + 3) + 1] = pos.y - (0 - j) * physicalSize.y / 6;
				vertices[4 * (v + 3) + 2] = tex.x; 
				vertices[4 * (v + 3) + 3] = tex.y - textureSize.y; // rrrr!!					
				
				// two triangles
				indices[ind++] = (short) v;
				indices[ind++] = (short) (v + 1);
				indices[ind++] = (short) (v + 2);
				
				indices[ind++] = (short) v;
				indices[ind++] = (short) (v + 2);
				indices[ind++] = (short) (v + 3);
				
				v += 4;
	
			}
		}
		
		//Gdx.app.error("", "x = " + vertices[0] + " , y = " + vertices[1]);
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
	} // buildMesh

	void render()  {
		
		buildMesh();
		
		GL10 gl = Gdx.graphics.getGL10();
		Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        texture.bind();
        
        gl.glPushMatrix();
        gl.glTranslatef(0.3f, 0.0f, 0.0f);
        //gl.glScalef(0.5f, 0.5f, 1.0f);
        gl.glRotatef(0, 0f, 0f, 1f);
        mesh.render(GL10.GL_TRIANGLES);
        gl.glPopMatrix();
	}
	
	private void join(Body b1, Body b2, float length) {
		
		DistanceJointDef jd = new DistanceJointDef(); 
		jd.localAnchorA.set(0, 0);
		jd.localAnchorB.set(0, 0);
		jd.bodyA = b1;
		jd.bodyB = b2;
		jd.collideConnected = true;
		//jd.maxLength = 10 + 2 * i;
		jd.length = length;
		//jd.maxForce = mass * gravity;
		//jd.maxTorque = mass * radius * gravity;
		jd.dampingRatio = 0.5f;
		jd.frequencyHz = 6;
		world.createJoint(jd);
	}
	
	void merge(PhysicalCell peer, int dir) {
		
		DistanceJointDef jd = new DistanceJointDef();
		jd.length = physicalSize.x / 3;
		jd.collideConnected = false;
		//jd.dampingRatio = 0.5f;
		//jd.frequencyHz = 6;
		jd.localAnchorA.set(0, 0);
		jd.localAnchorB.set(0, 0);
		
		if (dir == 1) { // right TODO: fix it


			jd.bodyA = this.bodies[2][0];
			jd.bodyB = peer.bodies[0][0];
			world.createJoint(jd);

			jd.bodyA = this.bodies[2][1];
			jd.bodyB = peer.bodies[0][1];
			world.createJoint(jd);
			
			jd.bodyA = this.bodies[2][2];
			jd.bodyB = peer.bodies[0][2];
			world.createJoint(jd);
		}
	}
	
	public void applyForce(int i, int j, Vector2 force) {
		bodies[i][j].applyForce(force, new Vector2(0, 0));
	}

	public void applyLinearImpulse(int i, int j, Vector2 force) {
		bodies[i][j].applyLinearImpulse(force, new Vector2(0, 0));
	}
	
	public void setType(int i, int j, BodyType type) {
		bodies[i][j].setType(type);
	}
	
}

