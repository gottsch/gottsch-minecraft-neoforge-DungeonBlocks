// Copied verbatim from Monster Manual (GMM) 1.20.1:
//   src/main/java/mod/gottsch/forge/gmm/core/client/model/GargoyleModel.java, createBodyLayer()
// tools/entity_model.py parses it to bake the gargoyle's cubes into the statues' OBJ models, so a
// statue is the mob itself, in stone. To follow a change to the mob, copy the method again.
public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition gargoyle = partdefinition.addOrReplaceChild("gargoyle", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
		PartDefinition body = gargoyle.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -16.0F, 6.5F, 0.1309F, 0.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.5F, -7.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -7.0F, -3.0F, -0.3491F, 0.0F, 0.0F));
		PartDefinition topTeeth2_r1 = head.addOrReplaceChild("topTeeth2_r1", CubeListBuilder.create().texOffs(32, 64).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, 0.9F, -6.7F, 0.0F, 0.0F, 0.7854F));
		PartDefinition topTeeth1_r1 = head.addOrReplaceChild("topTeeth1_r1", CubeListBuilder.create().texOffs(32, 64).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.9F, -6.7F, 0.0F, 0.0F, 0.7854F));
		PartDefinition rightEar_r1 = head.addOrReplaceChild("rightEar_r1", CubeListBuilder.create().texOffs(32, 58).mirror().addBox(0.0F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, -1.75F, -4.0F, 0.3491F, -0.3054F, 0.0F));
		PartDefinition leftEar_r1 = head.addOrReplaceChild("leftEar_r1", CubeListBuilder.create().texOffs(32, 58).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.75F, -4.0F, 0.3491F, 0.3054F, 0.0F));
		PartDefinition rightHorn = head.addOrReplaceChild("rightHorn", CubeListBuilder.create().texOffs(32, 53).mirror().addBox(-5.0F, -0.5F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -4.5F, -3.0F, -0.7854F, 0.0F, 0.3054F));
		PartDefinition leftHorn = head.addOrReplaceChild("leftHorn", CubeListBuilder.create().texOffs(32, 53).addBox(0.0F, -0.5F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -4.5F, -3.0F, -0.7854F, 0.0F, -0.3054F));
		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(25, 26).addBox(-4.0F, -0.5F, -5.75F, 8.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.5F, 0.3054F, 0.0F, 0.0F));
		PartDefinition smallTeeth1_r1 = jaw.addOrReplaceChild("smallTeeth1_r1", CubeListBuilder.create().texOffs(19, 43).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, -5.7F, 0.0F, 0.0F, 0.7854F));
		PartDefinition rightCanine_r1 = jaw.addOrReplaceChild("rightCanine_r1", CubeListBuilder.create().texOffs(19, 40).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, 0.0F, -5.7F, 0.0F, 0.0F, 0.7854F));
		PartDefinition leftCanine_r1 = jaw.addOrReplaceChild("leftCanine_r1", CubeListBuilder.create().texOffs(19, 40).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, 0.0F, -5.7F, 0.0F, 0.0F, 0.7854F));
		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 26).addBox(-3.0F, -9.0F, -2.0F, 8.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 63).addBox(-3.0F, -4.0F, -2.75F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offset(-2.0F, 6.0F, 0.0F));
		PartDefinition loincloth_r1 = torso.addOrReplaceChild("loincloth_r1", CubeListBuilder.create().texOffs(54, 26).addBox(-3.0F, 0.0F, -0.1F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.0F, -2.0F, -0.2618F, 0.0F, 0.0F));
		PartDefinition rightAbs_r1 = torso.addOrReplaceChild("rightAbs_r1", CubeListBuilder.create().texOffs(56, 55).mirror().addBox(-0.15F, -5.0F, -0.0303F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(56, 55).addBox(2.35F, -5.0F, -0.0303F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -3.0F, -2.25F, 0.1745F, 0.0F, 0.0F));
		PartDefinition hump1_r1 = torso.addOrReplaceChild("hump1_r1", CubeListBuilder.create().texOffs(60, 14).addBox(-2.0F, -3.0F, -1.5F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -14.0F, -1.5F, 0.7418F, 0.0F, 0.0F));
		PartDefinition chest = torso.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 89).addBox(-6.5F, -7.0F, -2.5F, 13.0F, 5.0F, 5.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(1.0F, -7.0F, 0.0F, 0.3054F, 0.0F, 0.0F));
		PartDefinition rightWing = chest.addOrReplaceChild("rightWing", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -4.0F, 2.5F, 0.0436F, 0.3927F, 0.7854F));
		PartDefinition rightWingAxis = rightWing.addOrReplaceChild("rightWingAxis", CubeListBuilder.create().texOffs(25, 82).addBox(-11.5F, -0.5F, 0.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.02F))
				.texOffs(33, 0).addBox(-11.5F, -0.5F, 0.5F, 12.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(13, 49).addBox(-11.5F, 0.5F, 0.0F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition rightWingClaw = rightWingAxis.addOrReplaceChild("rightWingClaw", CubeListBuilder.create().texOffs(67, 26).mirror().addBox(-0.5F, -3.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-11.0F, 0.0F, 0.5F, 0.0F, 0.7854F, 0.0F));
		PartDefinition rightWingMedius = rightWingAxis.addOrReplaceChild("rightWingMedius", CubeListBuilder.create().texOffs(35, 1).addBox(0.0F, -0.5F, -11.5F, 0.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(1, 72).addBox(-0.5F, -0.5F, -11.5F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(-11.0F, 0.0F, 0.5F, 0.0F, 0.3054F, 0.0F));
		PartDefinition leftWing = chest.addOrReplaceChild("leftWing", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -4.0F, 2.5F, -0.0436F, -0.3927F, -0.7854F));
		PartDefinition leftWingAxis = leftWing.addOrReplaceChild("leftWingAxis", CubeListBuilder.create().texOffs(25, 82).addBox(-0.5F, -0.5F, 0.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.02F))
				.texOffs(33, 0).mirror().addBox(-0.5F, -0.5F, 0.5F, 12.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(13, 49).addBox(10.5F, 0.5F, 0.0F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition leftWingClaw = leftWingAxis.addOrReplaceChild("leftWingClaw", CubeListBuilder.create().texOffs(67, 26).addBox(-2.5F, -3.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 0.0F, 0.5F, 0.0F, -0.7854F, 0.0F));
		PartDefinition leftWingMedius = leftWingAxis.addOrReplaceChild("leftWingMedius", CubeListBuilder.create().texOffs(35, 1).addBox(0.0F, -0.5F, -11.5F, 0.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(1, 72).addBox(-0.5F, -0.5F, -11.5F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(11.0F, 0.0F, 0.5F, 0.0F, -0.3054F, 0.0F));
		PartDefinition arms = chest.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition leftArm = arms.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(48, 35).addBox(0.0F, -1.5F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -5.0F, 0.5F, -0.7854F, -0.1309F, 0.0F));
		PartDefinition leftForeArm = leftArm.addOrReplaceChild("leftForeArm", CubeListBuilder.create().texOffs(0, 49).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 5.5F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition leftHand = leftForeArm.addOrReplaceChild("leftHand", CubeListBuilder.create(), PartPose.offset(-7.5F, -0.5F, -0.5F));
		PartDefinition leftThumb_r1 = leftHand.addOrReplaceChild("leftThumb_r1", CubeListBuilder.create().texOffs(25, 59).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 6.5F, -0.5F, 0.0F, 0.0F, 0.2618F));
		PartDefinition leftClaw2_r1 = leftHand.addOrReplaceChild("leftClaw2_r1", CubeListBuilder.create().texOffs(18, 59).addBox(0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 6.5F, 1.0F, 0.0F, -0.4363F, 0.4363F));
		PartDefinition leftClaw1_r1 = leftHand.addOrReplaceChild("leftClaw1_r1", CubeListBuilder.create().texOffs(18, 59).addBox(0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 6.5F, 0.0F, 0.0F, 0.4363F, 0.6545F));
		PartDefinition rightArm = arms.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(48, 35).mirror().addBox(-3.0F, -1.5F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.0F, -5.0F, 0.5F, -0.7854F, 0.1309F, 0.0F));
		PartDefinition rightForeArm = rightArm.addOrReplaceChild("rightForeArm", CubeListBuilder.create().texOffs(0, 49).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, 5.5F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition rightHand = rightForeArm.addOrReplaceChild("rightHand", CubeListBuilder.create(), PartPose.offset(-7.5F, -0.5F, -0.5F));
		PartDefinition rightThumb_r1 = rightHand.addOrReplaceChild("rightThumb_r1", CubeListBuilder.create().texOffs(25, 59).mirror().addBox(0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.0F, 6.5F, -0.5F, 0.0F, 0.0F, 0.2618F));
		PartDefinition rightClaw2_r1 = rightHand.addOrReplaceChild("rightClaw2_r1", CubeListBuilder.create().texOffs(18, 59).mirror().addBox(-3.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(7.0F, 6.5F, 1.0F, 0.0F, 0.4363F, -0.2618F));
		PartDefinition rightClaw1_r1 = rightHand.addOrReplaceChild("rightClaw1_r1", CubeListBuilder.create().texOffs(18, 59).mirror().addBox(-3.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(7.0F, 6.5F, 0.0F, 0.0F, -0.4363F, -0.7854F));
		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 4.5F, 0.5F, 0.3927F, 0.0F, 0.0F));
		PartDefinition upperTail = tail.addOrReplaceChild("upperTail", CubeListBuilder.create().texOffs(19, 47).addBox(-1.5F, -0.5F, -2.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.3446F, 0.7325F));
		PartDefinition lowerTail = tail.addOrReplaceChild("lowerTail", CubeListBuilder.create().texOffs(47, 55).addBox(-1.0F, -0.1453F, -1.077F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.3446F, 0.2325F, 0.2182F, 0.0F, 0.0F));
		PartDefinition legs = gargoyle.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(16.0F, 0.0F, 6.0F));
		PartDefinition rightLeg = legs.addOrReplaceChild("rightLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(-20.0F, -11.0F, 1.0F, -0.2618F, 0.2182F, 0.0F));
		PartDefinition rightThigh = rightLeg.addOrReplaceChild("rightThigh", CubeListBuilder.create().texOffs(25, 35).mirror().addBox(-1.5F, -3.0F, 0.2F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 4.0F, -7.0F, 0.3491F, 0.0F, 0.0F));
		PartDefinition rightCalf = rightLeg.addOrReplaceChild("rightCalf", CubeListBuilder.create().texOffs(51, 46).mirror().addBox(-1.5F, -6.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 9.0F, -4.0F, 0.48F, 0.0F, 0.0F));
		PartDefinition rightAnkle = rightLeg.addOrReplaceChild("rightAnkle", CubeListBuilder.create(), PartPose.offset(12.0F, 11.0F, -1.0F));
		PartDefinition rightFoot = rightAnkle.addOrReplaceChild("rightFoot", CubeListBuilder.create().texOffs(58, 8).addBox(-0.5F, 2.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -3.0F, -1.0F, -0.0436F, 0.0F, 0.0F));
		PartDefinition toe3_r1 = rightFoot.addOrReplaceChild("toe3_r1", CubeListBuilder.create().texOffs(58, 8).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.5F, -4.0F, 0.0F, -0.2182F, 0.0F));
		PartDefinition toe4_r1 = rightFoot.addOrReplaceChild("toe4_r1", CubeListBuilder.create().texOffs(58, 8).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.5F, -4.0F, 0.0F, 0.2182F, 0.0F));
		PartDefinition foot_r1 = rightFoot.addOrReplaceChild("foot_r1", CubeListBuilder.create().texOffs(0, 40).mirror().addBox(-2.0F, -2.0F, 0.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offsetAndRotation(0.5F, 3.0F, -4.0F, 0.6109F, 0.0F, 0.0F));
		PartDefinition leftLeg = legs.addOrReplaceChild("leftLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(-12.0F, -11.0F, 1.0F, -0.2618F, -0.2182F, 0.0F));
		PartDefinition leftThigh = leftLeg.addOrReplaceChild("leftThigh", CubeListBuilder.create().texOffs(25, 35).addBox(-1.5F, -3.0F, 0.2F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -7.0F, 0.3491F, 0.0F, 0.0F));
		PartDefinition leftCalf = leftLeg.addOrReplaceChild("leftCalf", CubeListBuilder.create().texOffs(51, 46).addBox(-1.5F, -6.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 9.0F, -4.0F, 0.48F, 0.0F, 0.0F));
		PartDefinition leftAnkle = leftLeg.addOrReplaceChild("leftAnkle", CubeListBuilder.create(), PartPose.offset(12.0F, 11.0F, -1.0F));
		PartDefinition leftFoot = leftAnkle.addOrReplaceChild("leftFoot", CubeListBuilder.create().texOffs(58, 8).addBox(-0.5F, 2.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -3.0F, -1.0F, -0.0436F, 0.0F, 0.0F));
		PartDefinition leftToe4_r1 = leftFoot.addOrReplaceChild("leftToe4_r1", CubeListBuilder.create().texOffs(58, 8).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.5F, -4.0F, 0.0F, -0.2182F, 0.0F));
		PartDefinition leftToe5_r1 = leftFoot.addOrReplaceChild("leftToe5_r1", CubeListBuilder.create().texOffs(58, 8).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.5F, -4.0F, 0.0F, 0.2182F, 0.0F));
		PartDefinition foot_r2 = leftFoot.addOrReplaceChild("foot_r2", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -2.0F, 0.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.5F, 3.0F, -4.0F, 0.6109F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
}
