
//<editor-fold defaultstate="collapsed" desc="make ceilingSlab.obj">
private void make_ceilingSlab(BufferSet buffer,
            Block block, BlockData data, Block[] neighbors,
            int x, int y, int z) {
BlockTexture.FaceTexture texture;


if(sideIsVisible(block, neighbors[NEG_X])){
	texture = block.texture.getNEG_X();
	//NEG_X FACE:
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)-1, texture);

	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-1, texture);
}


	texture = block.texture.getPOS_Y();
	buffer.vertex(-0.96f + x,	0.96f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ 0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ -0.0f,0.0f, /* normal */ (byte)0, texture);

	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ -0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.96f + y,	z,	 /* uvs */ 1.0f,1.0f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.96f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ 0.0f,1.0f, /* normal */ (byte)0, texture);

	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ -0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ 0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.0f, /* normal */ (byte)0, texture);


if(sideIsVisible(block, neighbors[NEG_Z])){
	texture = block.texture.getNEG_Z();
	//NEG_Z FACE:
	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-3, texture);
	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)-3, texture);

	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.96f + x,	0.96f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-3, texture);
}}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="make floorSlab.obj">
private void make_floorSlab(BufferSet buffer,
            Block block, BlockData data, Block[] neighbors,
            int x, int y, int z) {
BlockTexture.FaceTexture texture;


if(sideIsVisible(block, neighbors[NEG_X])){
	texture = block.texture.getNEG_X();
	//NEG_X FACE:
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)-1, texture);

	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-1, texture);
}


	texture = block.texture.getPOS_Y();
	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	y,	z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	y,	-0.96f + z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ -0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ 1.0f,1.0f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ -0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.48f + y,	-0.96f + z,	 /* uvs */ 0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.0f, /* normal */ (byte)0, texture);


if(sideIsVisible(block, neighbors[NEG_Y])){
	texture = block.texture.getNEG_Y();
	//NEG_Y FACE:
	buffer.vertex(-0.96f + x,	y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 0.0f,1.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ -0.0f,0.0f, /* normal */ (byte)-2, texture);

	buffer.vertex(-0.96f + x,	y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(-0.96f + x,	y,	-0.96f + z,	 /* uvs */ 1.0f,1.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 0.0f,1.0f, /* normal */ (byte)-2, texture);
}

if(sideIsVisible(block, neighbors[NEG_Z])){
	texture = block.texture.getNEG_Z();
	//NEG_Z FACE:
	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.96f + x,	y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-3, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)-3, texture);

	buffer.vertex(x,	0.48f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.96f + x,	0.48f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.96f + x,	y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-3, texture);
}}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="make sideSlab.obj">
private void make_sideSlab(BufferSet buffer,
            Block block, BlockData data, Block[] neighbors,
            int x, int y, int z) {
BlockTexture.FaceTexture texture;



	texture = block.texture.getPOS_Y();
	buffer.vertex(-0.48f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.48f + x,	y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.48f + x,	0.96f + y,	z,	 /* uvs */ -0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.48f + x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.48f + x,	y,	z,	 /* uvs */ 1.0f,1.0f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.48f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.48f + x,	0.96f + y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.48f + x,	y,	-0.96f + z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.48f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)0, texture);

	buffer.vertex(-0.48f + x,	0.96f + y,	z,	 /* uvs */ -0.0f,1.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.48f + x,	0.96f + y,	-0.96f + z,	 /* uvs */ 0.0f,-0.0f, /* normal */ (byte)0, texture);
	buffer.vertex(-0.48f + x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.0f, /* normal */ (byte)0, texture);


if(sideIsVisible(block, neighbors[NEG_X])){
	texture = block.texture.getNEG_X();
	//NEG_X FACE:
	buffer.vertex(x,	y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ 0.0f,1.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ -0.0f,0.0f, /* normal */ (byte)-1, texture);

	buffer.vertex(x,	y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 1.0f,1.0f, /* normal */ (byte)-1, texture);
	buffer.vertex(x,	0.96f + y,	-0.96f + z,	 /* uvs */ 0.0f,1.0f, /* normal */ (byte)-1, texture);
}

if(sideIsVisible(block, neighbors[NEG_Y])){
	texture = block.texture.getNEG_Y();
	//NEG_Y FACE:
	buffer.vertex(-0.48f + x,	y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-2, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)-2, texture);

	buffer.vertex(-0.48f + x,	y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(-0.48f + x,	y,	-0.96f + z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-2, texture);
	buffer.vertex(x,	y,	-0.96f + z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-2, texture);
}

if(sideIsVisible(block, neighbors[NEG_Z])){
	texture = block.texture.getNEG_Z();
	//NEG_Z FACE:
	buffer.vertex(-0.48f + x,	0.96f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-3, texture);
	buffer.vertex(x,	0.96f + y,	z,	 /* uvs */ -0.0f,0.5f, /* normal */ (byte)-3, texture);

	buffer.vertex(-0.48f + x,	0.96f + y,	z,	 /* uvs */ -0.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(-0.48f + x,	y,	z,	 /* uvs */ 1.0f,-0.0f, /* normal */ (byte)-3, texture);
	buffer.vertex(x,	y,	z,	 /* uvs */ 1.0f,0.5f, /* normal */ (byte)-3, texture);
}}
//</editor-fold>



private static boolean sideIsVisible(Block thisBlock, Block side){
    return side.opaque != thisBlock.opaque;
}