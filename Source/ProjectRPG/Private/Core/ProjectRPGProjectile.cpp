// Copyright 1998-2014 Epic Games, Inc. All Rights Reserved.

#include "ProjectRPG.h"
#include "ProjectRPGProjectile.h"


AProjectRPGProjectile::AProjectRPGProjectile(const class FPostConstructInitializeProperties& PCIP)
: Super(PCIP)
{
    // Use a sphere as a simple collision representation
    CollisionComp = PCIP.CreateDefaultSubobject<USphereComponent>(this, TEXT("SphereComp"));
    CollisionComp->InitSphereRadius(5.0f);
    CollisionComp->BodyInstance.SetCollisionProfileName("Projectile"); // Collision profiles are defined in DefaultEngine.ini
    CollisionComp->OnComponentHit.AddDynamic(this, &AProjectRPGProjectile::OnHit); // set up a notification for when this component overlaps something
    RootComponent = CollisionComp;

    // Use a ProjectileMovementComponent to govern this projectile's movement
    ProjectileMovement = PCIP.CreateDefaultSubobject<UProjectileMovementComponent>(this, TEXT("ProjectileComp"));
    ProjectileMovement->UpdatedComponent = CollisionComp;
    ProjectileMovement->InitialSpeed = 3000.f;
    ProjectileMovement->MaxSpeed = 3000.f;
    ProjectileMovement->bRotationFollowsVelocity = true;
    ProjectileMovement->bShouldBounce = true;

    // Die after 3 seconds by default
    InitialLifeSpan = 10.0f;
}

void AProjectRPGProjectile::OnHit(AActor* OtherActor, UPrimitiveComponent* OtherComp, FVector NormalImpulse, const FHitResult& Hit)
{
    if ((OtherActor != NULL) && (OtherActor != this) && (OtherComp != NULL))
    {
        FPointDamageEvent pointDamage;
        // pointDamage.DamageTypeClass -- Need to make some damage types
        pointDamage.HitInfo = Hit;
        pointDamage.ShotDirection = NormalImpulse;
        pointDamage.Damage = 100.0f;

        OtherActor->TakeDamage(pointDamage.Damage, pointDamage, ControllingPawn->GetController(), this);
        Destroy();
    }
}